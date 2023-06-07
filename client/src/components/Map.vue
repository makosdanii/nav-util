<template>
  <v-switch class="margin" v-if="role === 'user'" v-model="markersVisible" color="blue"
            @click="toggleMarkers"
            :label="`Markers are ${markersVisible ? 'displayed':'hidden'}`"></v-switch>
  <v-spacer/>
  <v-dialog v-model="routeDialog" max-width="500px">
    <template #activator="{props}">
      <v-btn class="margin" v-show="saveVisible" v-bind="props" append-icon="mdi-navigation-variant">Save</v-btn>
    </template>
    <v-card>
      <form @submit.prevent="saveRoute">
        <v-card-title>
          <span class="text-h5">New route</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-text-field name="name" label="Name"/>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="cancel">
            Cancel
          </v-btn>
          <v-btn
              color="blue darken-1"
              text
              type="submit"
          >
            Save
          </v-btn>
        </v-card-actions>
      </form>
    </v-card>
  </v-dialog>
  <v-dialog v-model="markerDialog" max-width="500px">
    <v-card>
      <form @submit.prevent="saveMarker">
        <v-card-title>
          <span class="text-h5">New marker</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6" md="4">
                <v-text-field name="name" v-model="editedItem.name" label="Name" :error-messages="errors.name"/>
                <v-text-field v-model="editedItem.lng" label="Longitude" :disabled="true"/>
                <v-text-field v-model="editedItem.lat" label="Latitude" :disabled="true"/>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <v-select
                    v-model="editedItem.idx"
                    :error-messages="errors.idx"
                    :items="markerTitles"
                    label="Select marker"
                >
                  <template v-slot:selection="{ item }">
                    {{ item.raw }}
                  </template>
                  <template v-slot:item="{ item }">
                    <div style="display: flex;" @click="selected(item.raw)">
                      <h3 class="margin">{{ item.raw }}</h3>
                      <span style="margin-left: auto" :ref="item.raw">
                        {{ renderMarkerSelect(item.raw) }}
                      </span>
                    </div>
                  </template>
                </v-select>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="cancel">
            Cancel
          </v-btn>
          <v-btn
              color="blue darken-1"
              text
              type="submit"
          >
            Save
          </v-btn>
        </v-card-actions>
      </form>
    </v-card>
  </v-dialog>
  <div id="map" class="mapboxgl-canvas-container"></div>
  <v-snackbar v-model="snack" :color="color" :timeout="3000">{{ snackText }}</v-snackbar>
</template>
<script>
import mapboxgl from 'mapbox-gl'
import server from '@/business/PizzaServerAPI'
import _ from 'lodash'
import * as yup from "yup";
import dotenv from 'dotenv';

const editedItem = {
  name: "",
  idx: "",
  lng: 0,
  lat: 0,
}

let route = {}
let markers = []
let start = []
let end = []

const token = "MAPBOX_ACCESS_TOKEN"
console.log(token)
const url = 'mapbox://styles/mapbox/streets-v12'
mapboxgl.accessToken = token
let map = null

export default {
  inject: ['markers', 'listMarkers', 'markerTitles', 'role'],
  data() {
    return {
      name: "Map",
      editedItem: _.cloneDeep(editedItem),
      errors: {},
      markerDialog: false,
      routeDialog: false,
      schema: yup.object({
        name: yup.string().required(),
        idx: yup.number().min(1).max(25).required(),
        lng: yup.number().required(),
        lat: yup.number().required(),
      }),
      snack: false,
      snackText: "",
      color: "",
      markersVisible: false,
      saveVisible: false,
    }
  },
  async mounted() {
    map = new mapboxgl.Map({
      container: 'map',
      style: url,
      center: [19.047, 47.493],
      zoom: 12.85,
      pitch: 70.0,
      bearing: 15,
    })

    map.on('click', (e) => {
      if ('user' !== this.role) return;
      if (this.markersVisible) {
        this.editedItem.lng = e.lngLat.lng
        this.editedItem.lat = e.lngLat.lat
        this.markerDialog = true
      } else {
        const coords = Object.keys(e.lngLat).map(key => e.lngLat[key])
        //after clicking third, discard both previous coords of clicks
        if (!start.length) {
          start = coords
          this.displayCoord(false)
        } else if (!end.length) {
          end = coords
          this.displayCoord(true) // true means that after displaying coords, it can display the route
          this.saveVisible = true
        } else {
          if (map.getLayer('start'))
            map.setLayoutProperty('start', 'visibility', 'none');

          if (map.getLayer('end'))
            map.setLayoutProperty('end', 'visibility', 'none');

          if (map.getLayer('route'))
            map.setLayoutProperty('route', 'visibility', 'none');

          if (map.getLayer('start'))
            map.setLayoutProperty('start', 'visibility', 'none');

          start = []
          end = []
          this.saveVisible = false;
        }
      }
    });
  },
  methods: {
    renderMarkerSelect(item) {
      const span = this.$refs[item]
      const idx = this.markerTitles.findIndex(title => title === item)
      if (span && span.childNodes.length < 1)
        span.appendChild(server.loadSprite(idx + 1))
    },
    selected(value) {
      this.editedItem.idx = this.markerTitles.findIndex(title => title === value) + 1
    },
    saveMarker() {
      this.errors = {}
      this.schema.validate(this.editedItem, {abortEarly: false})
          .then(() => {
            server.addMarker(this.editedItem)
                .then(async (promise) => {
                  if (promise.status === 201) {
                    this.addMarkersToMap()
                    this.snackText = "Created"
                    this.color = "green"
                    this.snack = true
                  }
                }).catch(err => {
              if (err.response.status === 400 || err.response.status === 401 || err.response.status === 404) {
                this.snackText = "Operation denied"
                this.color = "red"
                this.snack = true
              }
            })
            this.cancel()
          })
          .catch((err) => {
            err.inner.forEach((error) => {
              this.errors = {...this.errors, [error.path.split('.')[0]]: error.message};
            });
          });
    },
    saveRoute(values) {
      if (values.target.name.value.length) {
        server.addRoute({name: values.target.name.value, route: JSON.stringify(route)}).then(promise => {
          if (promise.status === 201) {
            this.snackText = "Created"
            this.color = "green"
            this.snack = true
          }
        }).catch(err => {
          if (err.response.status === 400 || err.response.status === 401 || err.response.status === 404) {
            this.snackText = "Operation denied"
            this.color = "red"
            this.snack = true
          }
        })
      }
      this.cancel()
      this.routeDialog = false
    },
    cancel() {
      this.editedItem = _.cloneDeep(editedItem)
      route = {}
      this.markerDialog = false
      this.routeDialog = false
    },
    async addMarkersToMap() {
      this.markersVisible = true
      const SIZE = 250
      const LENGTH = 5
      const BLOCK = SIZE / LENGTH
      const MARKER_SIZE = 416

      await this.listMarkers()
      this.markers.forEach(marker => {
        const startX = BLOCK * ((marker.idx - 1) % LENGTH)
        const startY = BLOCK * Math.floor((marker.idx - 1) / LENGTH)

        const spriteImg = new Image();
        const markerImg = new Image();

        const canvas = document.createElement("canvas")
        canvas.width = MARKER_SIZE;
        canvas.height = MARKER_SIZE;
        const canvasContext = canvas.getContext('2d');
        markerImg.onload = function () {
          canvasContext.drawImage(markerImg, 0, 0);
          spriteImg.onload = function () {
            canvasContext.drawImage(spriteImg, startX, startY, BLOCK, BLOCK, 184, 80, BLOCK, BLOCK)

            const newMarker = new mapboxgl.Marker({element: canvas})
                .setLngLat([marker.lng, marker.lat])
            if (!markers.includes(newMarker)) {
              newMarker.addTo(map);
              markers.push(newMarker)
            }
          }
          spriteImg.src = 'src/assets/marker_sprite.png';
        }
        markerImg.src = 'src/assets/marker.png';

      })
    },
    toggleMarkers() {
      if (this.markersVisible) {
        markers.forEach(marker => marker.remove())
        markers = []
      } else {
        start = []
        end = []
        this.addMarkersToMap()
        if (map.getLayer('start'))
          map.setLayoutProperty('start', 'visibility', 'none');

        if (map.getLayer('end'))
          map.setLayoutProperty('end', 'visibility', 'none');

        if (map.getLayer('route'))
          map.setLayoutProperty('route', 'visibility', 'none');

      }
    },
    displayCoord(complete) {
      if (!start.length) return

      // first element will be start point, last will be end point, which sets the route as well
      const point = {
        type: 'FeatureCollection',
        features: [
          {
            type: 'Feature',
            properties: {},
            geometry: {
              type: 'Point',
              coordinates: complete ? end : start
            }
          }
        ]
      };

      // update layers under corresponding id
      const id = complete ? 'end' : 'start'
      //check if the layer has already been set for a previous route
      if (map.getLayer(id)) {
        map.getSource(id).setData(point);
        map.setLayoutProperty(id, 'visibility', 'visible');
      } else {
        map.addLayer({
          id: id,
          type: 'circle',
          source: {
            type: 'geojson',
            data: point
          },
          paint: {
            'circle-radius': 10,
            'circle-color': '#3887be'
          }
        })
      }
      ;

      if (complete) {
        this.getRoute()
      }
    },
    getRoute: async function () {
      const query = await fetch(
          `https://api.mapbox.com/directions/v5/mapbox/driving/` +
          `${start[0]},${start[1]};${end[0]},${end[1]}` +
          `?steps=true&geometries=geojson&access_token=${mapboxgl.accessToken}`,
          {method: 'GET'}
      );

      const json = await query.json();
      const data = json.routes[0];
      const geojson = {
        type: 'Feature',
        properties: {},
        geometry: {
          type: 'LineString',
          coordinates: data.geometry.coordinates
        }
      };

      // if the route already exists on the map, we'll reset it using setData
      if (map.getLayer('route')) {
        map.getSource('route').setData(geojson);
        map.setLayoutProperty('route', 'visibility', 'visible');
      } else {
        map.addLayer({
          id: 'route',
          type: 'line',
          source: {
            type: 'geojson',
            data: geojson
          },
          layout: {
            'line-join': 'round',
            'line-cap': 'round'
          },
          paint: {
            'line-color': '#3887be',
            'line-width': 5,
            'line-opacity': 0.75
          }
        });
      }

      route = {
        distance: data.legs[0].distance, duration: data.legs[0].duration,
        steps: data.legs[0].steps.map(step =>
            step.name
        )
      }
    }
  }
}
</script>
<style>
@import 'mapbox-gl/dist/mapbox-gl.css';

.mapboxgl-canvas-container {
  height: 600px;
  width: 1200px;
}
</style>
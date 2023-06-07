<template>
  <DataTable
      name="Marker"
      :headers="headers"
      :items="markers"
      @save="save"
      @delete="erase"
      @edit="edit"
      @cancel="reset"
      ref="table"
      :cannot-create="true"
  >
    <template #card-container>
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
    </template>
  </DataTable>
</template>

<script>
import _ from "lodash";
import * as yup from "yup";
import server from "@/business/ServerAPI";
import DataTable from "@/components/DataTable.vue";

const defaultItem = {
  id: 0,
  name: "",
  idx: "",
  lng: 0,
  lat: 0,
}

export default {
  name: "MarkerPage",
  inject: ['markers', 'listMarkers', 'markerTitles'],
  components: {DataTable},
  data() {
    return {
      headers: [
        {title: "Name", key: "name"},
        {title: "Category", key: "idx"},
        {title: "Latitude", key: "lat"},
        {title: "Longitude", key: "lng"},
        {title: "Actions", key: "actions", sortable: false},
      ],
      editedItem: _.cloneDeep(defaultItem),
      errors: {},
      schema: yup.object({
        name: yup.string().required(),
        idx: yup.number().min(1).max(25).required(),
        lng: yup.number().required(),
        lat: yup.number().required(),
      }),
    }
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
    save() {
      this.errors = {}
      this.schema.validate(this.editedItem, {abortEarly: false})
          .then(() => {
            server.updateMarker(this.editedItem, this.editedItem.id)
                .then((promise) => {
                  if (promise.status === 201) {
                    this.listMarkers()
                    this.$refs.table.snackText = "Updated"
                    this.$refs.table.color = "green"
                    this.$refs.table.snack = true
                  }
                }).catch(err => {
              if (err.response.status === 400 || err.response.status === 401 || err.response.status === 404) {
                this.$refs.table.snackText = "Operation denied"
                this.$refs.table.color = "red"
                this.$refs.table.snack = true
              }
            })

            this.reset()
          })
          .catch((err) => {
            err.inner.forEach((error) => {
              this.errors = {...this.errors, [error.path.split('.')[0]]: error.message};
            });
          });
    },
    edit(item) {
      this.editedItem = _.cloneDeep(this.markers.find(marker => marker.id === item.id))
      this.$refs.table.dialog = true
    },
    erase(item) {
      if (confirm(`This marker will be deleted`)) {
        server.deleteMarker(item.id).then((promise) => {
          if (promise.status === 200) {
            this.listMarkers()
            this.$refs.table.snackText = "Deleted"
            this.$refs.table.color = "green"
            this.$refs.table.snack = true
          }
        }).catch(err => {
          if (err.response.status === 400 || err.response.status === 401 || err.response.status === 404) {
            this.$refs.table.snackText = "Operation denied"
            this.$refs.table.color = "red"
            this.$refs.table.snack = true
          }
        });
      }
    },
    reset() {
      this.editedItem = _.cloneDeep(defaultItem)
      this.$refs.table.dialog = false;
    }
  },
  mounted() {
    this.listMarkers();
  }
}
</script>

<style scoped>

</style>
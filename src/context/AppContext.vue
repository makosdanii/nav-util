<template>
  <div id="context-provider">
    <slot></slot>
  </div>
</template>

<script>
import server from "../business/PizzaServerAPI.js";
import {computed} from "vue";

export default {
  name: "AppContext",
  data() {
    return {
      role: "",
      users: [],
      markers: [],
      markerTitles: ['Hike', 'Sail', 'Bar', 'Selfie point', 'Beach', 'Hotel', 'Border', 'Trip', 'Miscellaneous', 'Lifeguard', 'Sunset', 'Pools', 'Travel agent', 'Auxiliaries', 'Camping', 'Tour Guide', 'Dockyard', 'Stargazing', 'Garage', 'Zoo', 'Airport', 'Underwear', 'Suburb', 'Theatre', 'Nightclub'],
    }
  },

  provide() {
    return {
      role: computed(() => this.role),
      users: computed(() => this.users),
      markers: computed(() => this.markers),
      markerTitles: this.markerTitles,
      listUsers: this.listUsers,
      listMarkers: this.listMarkers,
      login: this.login,
      logout: this.logout,
    }
  },
  mounted() {
    if (this.$cookies.isKey('role'))
      this.role = this.$cookies.get('role')
  },
  methods: {
    // cannot use arrow function if 'this' is needed in the scope
    async login(values) {
      await server.authenticate(values)
          .then(promise => {
            this.$cookies.set('userId', promise.data.id, '5h')
            this.$cookies.set('token', promise.data.token, '5h')
          }).catch((err) => console.log(err))
      await server.findUser().then(promise => {
        this.role = promise.data.roleByRoleId.name;
        this.$cookies.set('role', this.role, '5h')
      }).catch((err) => console.log(err))
    },
    async logout() {
      // await server.unset().then(promise => {
      //   if (promise.status === 200) {
      this.users = this.roles = this.cars = this.allergies = this.ingredients = this.orders = []
      this.role = ""
      this.$cookies.remove('userId')
      this.$cookies.remove('role')
      this.$cookies.remove('token')
      //   }
      // }).catch((err) => console.log(err));
      return !this.role.length
    },
    async listUsers() {
      await server.listUsers().then(promise => this.users = promise.data).catch((err) => console.log(err))
    },
    async listMarkers() {
      await server.findUser().then(promise => this.markers = promise.data.markersById)
    }
  },
}
</script>
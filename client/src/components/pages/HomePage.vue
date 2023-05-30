<template>
  <h2>{{ welcome }}</h2>
  <Map/>
  <v-snackbar v-model="snack" :color="color" :timeout="3000">{{ snackText }}</v-snackbar>
</template>

<script>
import Map from "@/components/Map.vue";
import server from "@/business/PizzaServerAPI.js"
import _ from "lodash";

export default {
  name: "HomePage",
  components: {Map},
  inject: ['role'],
  data() {
    return {
      snack: false,
      snackText: "",
      color: ""
    }
  },
  computed: {
    welcome() {
      return this.role.length ? "Welcome back!" : "Login first!"
    },
  }
}
</script>

<style scoped>
</style>
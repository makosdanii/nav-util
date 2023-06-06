<template>
  <h2 class="margin" v-if="!role.length">Login first!</h2>
  <Map/>
  <v-snackbar v-model="snack" :color="color" :timeout="3000">{{ snackText }}</v-snackbar>
</template>

<script>
import Map from "@/components/Map.vue";

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
}
</script>

<style scoped>
</style>
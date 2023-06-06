<template>
  <h2 class="margin">Duration: {{ Math.ceil(parsedRoute.duration / 60) }}&nbsp;min</h2>
  <h3 class="margin">Distance: {{ (parsedRoute.distance / 1000).toFixed(1) }}&nbsp;km</h3>
  <ul class="margin">
    <li style="display: inline" v-for="road in parsedRoute.steps">🚗{{ road }}&nbsp</li>
  </ul>
</template>

<script>
export default {
  name: "RouteExpandItem",
  props: {
    route: Object,
    width: Number
  },
  data() {
    return {
      parsedRoute: JSON.parse(this.route.route)
    }
  }
}
</script>

<style scoped>
@counter-style repeating-emoji {
  system: cyclic;
  symbols: "🚗";
  suffix: " ";
}

.repeating-counter-rule {
  list-style-type: repeating-emoji;
}
</style>
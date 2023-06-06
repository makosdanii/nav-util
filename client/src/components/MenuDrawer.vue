<template>
  <v-navigation-drawer
      expand-on-hover
      rail
      @update:rail="() => mini = !mini"
  >
    <v-list nav dense>
      <v-list-item
          @click="$router.push('/')"
          link
          prepend-icon="mdi-home"
      >
        <v-list-item-title>Home</v-list-item-title>
      </v-list-item>

      <v-list-item
          v-if="role === 'user'"
          @click="$router.push('/markers')"
          link
          prepend-icon="mdi-map-marker"
      >
        <v-list-item-title>Markers</v-list-item-title>
      </v-list-item>

      <v-list-item
          v-if="role === 'user'"
          @click="$router.push('/routes')"
          link
          prepend-icon="mdi-navigation-variant"
      >
        <v-list-item-title>Routes</v-list-item-title>
      </v-list-item>

      <v-list-item
          v-if="role"
          @click="role === 'admin' ? $router.push('/users') : $router.push('/user')"
          link
          prepend-icon="mdi-account"
      >
        <v-list-item-title>User account</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
export default {
  name: "MenuDrawer",
  inject: ['role'],
}
</script>

<style scoped>

</style>
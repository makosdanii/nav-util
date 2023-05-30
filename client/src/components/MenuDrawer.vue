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

      <v-divider v-if="role === 'admin'" class="mx-4" inset horizontal></v-divider>

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
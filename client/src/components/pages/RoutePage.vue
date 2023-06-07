<template>
  <DataTable
      name="Route"
      :headers="headers"
      :items="routes"
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
          <v-text-field name="name" v-model="editedItem.name" label="Name"/>
        </v-row>
      </v-container>
    </template>
    <template v-slot:expand="{item, width}">
      <RouteExpandItem :width="width" :route="item"/>
    </template>
  </DataTable>
</template>

<script>
import _ from 'lodash'
import server from "@/business/ServerAPI";
import DataTable from "@/components/DataTable.vue";
import RouteExpandItem from "@/components/RouteExpandItem.vue";

export default {
  name: "MarkerPage",
  components: {DataTable, RouteExpandItem},
  data() {
    return {
      headers: [
        {title: "Name", key: "name"},
        {title: "Actions", key: "actions", sortable: false},
        {title: "Details", key: 'data-table-expand'},
      ],
      routes: [],
      editedItem: {
        name: "",
        route: "",
        id: 0
      }
    }
  },
  methods: {
    save() {
      server.updateRoute(this.editedItem, this.editedItem.id)
          .then((promise) => {
            if (promise.status === 201) {
              server.findUser().then(promise => this.routes = promise.data.routesById)
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
    },
    edit(item) {
      this.editedItem = _.cloneDeep(this.routes.find(route => route.id === item.id))
      this.$refs.table.dialog = true
    },
    erase(item) {
      if (confirm(`This route will be deleted`)) {
        server.deleteRoute(item.id).then((promise) => {
          if (promise.status === 200) {
            server.findUser().then(promise => this.routes = promise.data.routesById)
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
      this.editedItem.name = ""
      this.$refs.table.dialog = false;
    }
  },
  mounted() {
    if (server.id()) {
      server.findUser().then(promise => this.routes = promise.data.routesById)
    }
  }
}
</script>

<style scoped>

</style>
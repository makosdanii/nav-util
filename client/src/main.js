import {createApp} from 'vue'
import {createVuetify} from 'vuetify'
import * as components from 'vuetify/components'
import * as lab from "vuetify/labs/components";
import * as directives from 'vuetify/directives'
import * as VueRouter from 'vue-router';
import VueCookies from 'vue-cookies';

import App from './App.vue'
import HomePage from "@/components/pages/HomePage.vue";
import UserPage from "@/components/pages/UserPage.vue";
import RegistrationPage from "@/components/pages/RegistrationPage.vue";
import MarkerPage from "@/components/pages/MarkerPage.vue";
import RoutePage from "@/components/pages/RoutePage.vue";
import _ from 'lodash';
import server from "@/business/ServerAPI.js";

import 'vuetify/styles'
import './assets/main.css'
import "material-design-icons-iconfont/dist/material-design-icons.css";
import "@mdi/font/css/materialdesignicons.css";

const routes = [
    {path: '/', component: HomePage},
    {path: '/user', component: RegistrationPage},
    {
        path: '/users', component: UserPage, beforeEnter: (to, from) => {
            if (server.role() !== 'admin')
                return '/'
        }
    },
    {
        path: '/markers', component: MarkerPage, beforeEnter: (to, from) => {
            if (server.role() !== 'user')
                return '/'
        }
    },
    {
        path: '/routes', component: RoutePage, beforeEnter: (to, from) => {
            if (server.role() !== 'user')
                return '/'
        }
    }
]

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes
})

const vuetify = createVuetify({
    components: {...components, ...lab},
    directives,
})

const app = createApp(App)
app.config.unwrapInjectedRef = true

app.use(router)
    .use(vuetify)
    .use(VueCookies)
    .use(_)
    .mount('#app')


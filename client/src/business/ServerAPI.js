import axios from "axios";
import VueCookies from 'vue-cookies';

const SERVER_URL = "http://localhost:8081";
// axios.defaults.withCredentials = true

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 3000,
});

const config = () => ({
    headers: {Authorization: VueCookies.isKey('token') ? `Bearer ${VueCookies.get('token')}` : ''}
})

const id = () => VueCookies.isKey('userId') ? VueCookies.get('userId') : 0

const role = () => VueCookies.isKey('role') ? VueCookies.get('role') : ''

const SIZE = 250
const LENGTH = 5
const BLOCK = SIZE / LENGTH

export default {
    id: () => id(),
    role: () => role(),
    authenticate: async (values) => await instance.post("authenticate", values),
    findUser: async () => await instance.get(`user/${id()}`, config()),
    listUsers: async () => await instance.get('user', config()),
    registerUser: async (values) => await instance.post('user/register', values, config()),
    updateUser: async (values) => await instance.put(`user/${values.id}`, values, config()),
    deleteUser: async (id) => await instance.delete(`user/${id ? id : id()}`, config()),
    listMarkers: async () => await instance.get(`marker`, config()),
    addMarker: async (marker) => await instance.post(`marker/${id()}`, marker, config()),
    updateMarker: async (marker, id) => await instance.put(`marker/${id}`, marker, config()),
    deleteMarker: async (id) => await instance.delete(`marker/${id}`, config()),
    addRoute: async (route) => await instance.post(`route/${id()}`, route, config()),
    updateRoute: async (route, id) => await instance.put(`route/${id}`, route, config()),
    deleteRoute: async (id) => await instance.delete(`route/${id}`, config()),
    loadSprite: function (idx) {
        const startX = BLOCK * ((idx - 1) % LENGTH) * -1
        const startY = BLOCK * Math.floor((idx - 1) / LENGTH) * -1

        const spriteImg = document.createElement('img');
        spriteImg.style.backgroundImage = 'url(/nav-util/assets/marker_sprite.png)';
        spriteImg.style.backgroundPositionX = `${startX}px`;
        spriteImg.style.backgroundPositionY = `${startY}px`;
        spriteImg.style.width = `${BLOCK}px`;
        spriteImg.style.height = `${BLOCK}px`;

        return spriteImg;
    },
};

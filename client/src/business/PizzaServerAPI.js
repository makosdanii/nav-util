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

export default {
    id: () => id(),
    role: () => role(),
    authenticate: async (values) => await instance.post("authenticate", values),
    findUser: async () => await instance.get(`user/${id()}`, config()),
    listUsers: async () => await instance.get('user', config()),
    registerUser: async (values) => await instance.post('user/register', values, config()),
    updateUser: async (values) => await instance.put(`user/${values.id}`, values, config()),
    deleteUser: async (id) => await instance.delete(`user/${id ? id : id()}`, config()),
};

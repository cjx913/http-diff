const Vue = window.Vue
const VueRouter = window.VueRouter
import { loadModule as _loadModule } from "./js/vue3-sfc-loader.esm.js"

const ElementPlus = window.ElementPlus

const VXETable = window.VXETable
VXETable.setup({
    table: {
        size: 'small',
        tooltipConfig: {
            theme: 'light',
            enterable: true
        },
    },
})

// import {loadModule} from "https://cdn.jsdelivr.net/npm/vue3-sfc-loader/dist/vue3-sfc-loader.esm.js"

const options = {
    moduleCache: {
        'vue': Vue,
        'vue-router': VueRouter,
        'element-plus': ElementPlus,
        'vxe-table': VXETable,
    },
    async getFile(url) {
        const res = await fetch(url);
        if (!res.ok)
            throw Object.assign(new Error(res.statusText + ' ' + url), {res});
        return {
            getContentData: asBinary => asBinary ? res.arrayBuffer() : res.text(),
        }
    },
    addStyle(textContent) {
        const style = Object.assign(document.createElement('style'), {textContent});
        const ref = document.head.getElementsByTagName('style')[0] || null;
        document.head.insertBefore(style, ref);
    },
}
const loadModule = (comp) => _loadModule(comp, options)

const routes = [
    {path: '/', redirect: '/result'},
    {name: 'result', path: '/result', component: () => loadModule('/src/views/result/index.vue')},
    {name: 'testcase', path: '/testcase', component: () => loadModule('/src/views/testcase/index.vue')},
    {path: '/about', component: () => loadModule('/src/views/About.vue')},
]
const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes,
})

const app = Vue.createApp({
    components: {
        'App': Vue.defineAsyncComponent(() => loadModule('/src/App.vue'))
    },
});
app.use(router)
    .use(ElementPlus)
    .use(VXETable)
    .component("vue-json-pretty", window.VueJsonPretty.default)
    .mount('#app');
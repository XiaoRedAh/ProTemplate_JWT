import {createRouter, createWebHistory} from "vue-router";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: ()=>import('@/pages/WelcomeIndex.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: ()=>import('@/components/welcome/Login.vue'),
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: ()=>import('@/components/welcome/Register.vue'),
                },
                {
                    path: 'reset',
                    name: 'welcome-reset',
                    component: ()=>import('@/components/welcome/ResetPaw.vue'),
                }
            ]
        }
    ]

})

export default router
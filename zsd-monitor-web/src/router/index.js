import { createRouter, createWebHistory } from 'vue-router'
import { unauthorized } from "@/net";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/views/welcome/LoginPage.vue')
                },
                {
                    path: 'forget',
                    name: 'welcome-forget',
                    component: () => import('@/views/welcome/ForgetPage.vue')
                }
            ]
        },
        {
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue'),
            children: [
                {
                    path: '',
                    name: 'manage',
                    component: () => import('@/views/main/Manage.vue')
                },
                {
                    path: 'security',
                    name: 'security',
                    component: () => import('@/views/main/Security.vue')
                },
                {
                    path: 'strategy-group', // 策略组路由
                    name: 'strategy-group',
                    component: () => import('@/views/main/StrategyGroup.vue') // 对应组件路径
                },
                {
                    path: 'create-Alarm',
                    name: 'create-Alarm',
                    component:()=>import('@/views/main/CreateAlarm.vue')
                },
                {
                    path: 'events-notifications',
                    name: 'events-notifications',
                    component: () => import('@/views/main/EventsNotifications.vue') // 新的组件
                }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const isUnauthorized = unauthorized()
    if (to.name.startsWith('welcome') && !isUnauthorized) {
        next('/index')
    } else if (to.fullPath.startsWith('/index') && isUnauthorized) {
        next('/')
    } else {
        next()
    }
})

export default router

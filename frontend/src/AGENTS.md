# Frontend Source

Vue 3 + TypeScript frontend. Composition API, Pinia state, Tailwind styling.

## Structure
```
src/
├── api/           # Centralized API functions (bookApi, borrowApi, etc.)
├── layouts/       # Main layout component with sidebar
├── router/        # Vue Router config with lazy loading
├── stores/        # Pinia stores (auth.ts only currently)
├── utils/         # request.ts (Axios interceptor for JWT)
├── views/         # Page components (Login, Books, Dashboard, etc.)
├── App.vue        # Root component
└── main.ts        # App entry point
```

## Component Pattern
```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { type Entity } from '@/api'  // Import types from api/index.ts

const items = ref<Entity[]>([])
onMounted(() => { /* init */ })
</script>

<template>
  <!-- Tailwind classes only - no custom CSS -->
  <div class="bg-white p-6">...</div>
</template>
```

## API Pattern
All API calls go through centralized functions in `api/index.ts`:
- Auto-sets JWT token via Axios interceptor in `utils/request.ts`
- Handle 401 by clearing token + redirecting to `/login`
- Error handling: `try/catch + alert(error.message || '操作失败')`

## State Management (Pinia)
Store pattern: `defineStore('id', () => { ... })`
- Persist tokens in localStorage
- Currently only `auth.ts` store exists

## Routing
- Lazy load: `() => import('@/views/Login.vue')`
- Protected routes: `meta.requiresAuth` in router config
- Navigation guard checks auth store

## TypeScript
- Strict mode enabled (`noUnusedLocals`, `noUnusedParameters`, etc.)
- Path alias: `@/*` → `./src/*`
- Types defined in `api/index.ts` (Book, BorrowRecord, User, etc.)

## Where to Look
| Task | Location |
|------|----------|
| Add new page | Create component in `views/`, add route in `router/index.ts` |
| Add new API endpoint | Add function in `api/index.ts` |
| Add state management | Create store in `stores/` |
| Style component | Use Tailwind utility classes only |

## Anti-Patterns
- **DO NOT** create custom CSS files - use Tailwind classes
- **NEVER** suppress TypeScript errors with `@ts-ignore` or `as any`
- **DO NOT** make direct Axios calls - use centralized API functions

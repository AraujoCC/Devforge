import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { AuthResponse, User } from '../types'

interface AuthState {
  accessToken: string | null
  user: User | null
  isAuthenticated: boolean
  login: (data: AuthResponse) => void
  logout: () => void
  setUser: (user: User) => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      user: null,
      isAuthenticated: false,
      login: (data: AuthResponse) => {
        localStorage.setItem('accessToken', data.accessToken)
        set({ accessToken: data.accessToken, isAuthenticated: true })
      },
      logout: () => {
        localStorage.removeItem('accessToken')
        set({ accessToken: null, user: null, isAuthenticated: false })
      },
      setUser: (user: User) => set({ user }),
    }),
    { name: 'auth-storage' }
  )
)
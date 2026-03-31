export interface User {
  id: number
  username: string
  email: string
  bio?: string
  avatarUrl?: string
  githubId?: string
  active: boolean
  createdAt: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  username: string
  email: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}
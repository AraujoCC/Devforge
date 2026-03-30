import { useState } from "react";
import { loginRequest } from "../api/auth";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();

    try {
      const data = await loginRequest(username, password);

      // salva token
      localStorage.setItem("accessToken", data.accessToken);

      alert("Login realizado com sucesso!");
      console.log(data);

    } catch (error) {
      alert("Erro no login");
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-900">
      <form
        onSubmit={handleLogin}
        className="bg-gray-800 p-8 rounded-2xl shadow-lg w-96"
      >
        <h2 className="text-2xl font-bold text-white mb-6 text-center">
          Login DevForge 🚀
        </h2>

        <input
          type="text"
          placeholder="Username"
          className="w-full p-3 mb-4 rounded bg-gray-700 text-white outline-none"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <input
          type="password"
          placeholder="Senha"
          className="w-full p-3 mb-6 rounded bg-gray-700 text-white outline-none"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button
          type="submit"
          className="w-full bg-blue-600 hover:bg-blue-700 text-white p-3 rounded font-bold"
        >
          Entrar
        </button>
      </form>
    </div>
  );
}
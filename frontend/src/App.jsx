import { useState } from "react";
import { api } from "./api/client";

const initialRegister = {
  orgId: "",
  email: "",
  fullName: "",
  password: "",
  role: "LAWYER",
};

const initialLogin = {
  orgId: "",
  email: "",
  password: "",
};

export default function App() {
  const [registerData, setRegisterData] = useState(initialRegister);
  const [loginData, setLoginData] = useState(initialLogin);
  const [token, setToken] = useState("");
  const [message, setMessage] = useState("");

  const onRegister = async (e) => {
    e.preventDefault();
    setMessage("Registering...");
    try {
      const { data } = await api.post("/auth/register", registerData);
      setToken(data.token || "");
      setMessage("Register successful");
    } catch (err) {
      setMessage(err?.response?.data?.error || "Register failed");
    }
  };

  const onLogin = async (e) => {
    e.preventDefault();
    setMessage("Logging in...");
    try {
      const { data } = await api.post("/auth/login", loginData);
      setToken(data.token || "");
      setMessage("Login successful");
    } catch (err) {
      setMessage(err?.response?.data?.error || "Login failed");
    }
  };

  return (
    <main style={{ fontFamily: "sans-serif", maxWidth: 980, margin: "40px auto", display: "grid", gap: 24 }}>
      <h1>Law Automation Auth Starter</h1>
      <p>{message}</p>
      <section style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 24 }}>
        <form onSubmit={onRegister} style={{ display: "grid", gap: 8 }}>
          <h2>Register</h2>
          <input placeholder="Org UUID" value={registerData.orgId} onChange={(e) => setRegisterData({ ...registerData, orgId: e.target.value })} required />
          <input placeholder="Full Name" value={registerData.fullName} onChange={(e) => setRegisterData({ ...registerData, fullName: e.target.value })} required />
          <input placeholder="Email" type="email" value={registerData.email} onChange={(e) => setRegisterData({ ...registerData, email: e.target.value })} required />
          <input placeholder="Password" type="password" value={registerData.password} onChange={(e) => setRegisterData({ ...registerData, password: e.target.value })} required />
          <select value={registerData.role} onChange={(e) => setRegisterData({ ...registerData, role: e.target.value })}>
            <option value="ADMIN">ADMIN</option>
            <option value="LAWYER">LAWYER</option>
            <option value="SECRETARY">SECRETARY</option>
          </select>
          <button type="submit">Register</button>
        </form>

        <form onSubmit={onLogin} style={{ display: "grid", gap: 8 }}>
          <h2>Login</h2>
          <input placeholder="Org UUID" value={loginData.orgId} onChange={(e) => setLoginData({ ...loginData, orgId: e.target.value })} required />
          <input placeholder="Email" type="email" value={loginData.email} onChange={(e) => setLoginData({ ...loginData, email: e.target.value })} required />
          <input placeholder="Password" type="password" value={loginData.password} onChange={(e) => setLoginData({ ...loginData, password: e.target.value })} required />
          <button type="submit">Login</button>
        </form>
      </section>

      <section>
        <h3>JWT Token</h3>
        <textarea readOnly value={token} style={{ width: "100%", minHeight: 120 }} />
      </section>
    </main>
  );
}

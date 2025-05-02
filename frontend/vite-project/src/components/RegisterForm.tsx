import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/RegisterForm.css";

const RegisterForm: React.FC = () => {
  const [username, setUsername]           = useState("");
  const [email, setEmail]                 = useState("");
  const [name, setName]                   = useState("");
  const [password, setPassword]           = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError]                 = useState<string | null>(null);
  const navigate                          = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
  
    if (password !== confirmPassword) {
      setError("Hasła muszą być takie same");
      return;
    }
  
    try {
      const res = await fetch("http://localhost:8080/info/create", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, name, password }),
      });
  
      if (res.ok) {
        navigate("/");
      } else {
        const errorText = await res.text();
        setError(errorText);
      }
    } catch {
      setError("Coś poszło nie tak — błąd połączenia");
    }
  };
  

  return (
    <div className="login-wrapper">
      <div className="register-container">
        <h2>Rejestracja</h2>
        <form onSubmit={handleSubmit}>
          {error && <div className="form-error">{error}</div>}
          <input
            type="text"
            placeholder="Nazwa użytkownika"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
          />
          <input
            type="email"
            placeholder="E-mail"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Imię i nazwisko"
            value={name}
            onChange={e => setName(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Hasło"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            minLength={8}
          />
          <input
            type="password"
            placeholder="Powtórz hasło"
            value={confirmPassword}
            onChange={e => setConfirmPassword(e.target.value)}
            required
            minLength={8}
          />
          <button type="submit">Zarejestruj się</button>
        </form>
      </div>
      <div className="login-panel">
        <h2>Masz już konto?</h2>
        <p>Zaloguj się, aby kontynuować.</p>
        <button onClick={() => navigate("/")}>Zaloguj się</button>
      </div>
    </div>
  );
};

export default RegisterForm;

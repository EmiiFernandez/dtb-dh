import React, { useState } from "react";
import styles from "./registro.module.css";

const Registro = () => {
  const [name, setName] = useState("");
  const [lastname, setLastname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (name.length < 1 || lastname.length < 1) {
      setError("Debes ingresar nombre/ apellido");
    } else if (!email.includes("@")) {
      setError(
        "El email ingresado no es valido, ingresa tu direccion nuevamente"
      );
    } else if (password.trim() === "") {
      setError("Debes ingresar tu contraseña sin espacios");
    } else if (password.length < 6) {
      setError("La contraseña debe tener al menos 6 caracteres");
    } else {
      try {
        const response = await fetch("http://18.118.140.140/signup", {
          method: "POST",
          body: JSON.stringify({
            name,
            lastname,
            email,
            password,
          }),
          headers: {
            "Content-type": "application/json; charset=UTF-8",
          },
        });

        if (response.status >= 200 && response.status < 300) {
          setSuccessMessage(
            `Registro exitoso ${name}. Chequea tu correo para validar el registro`
          );
          setError("");
        } else {
          setError("Hubo un error durante el registro. Por favor, inténtalo de nuevo");
        }
      } catch (error) {
        setError(
          "Ocurrió un error al registrar el usuario. Por favor, verifica tu conexión a internet"
        );
      }
    }

    setTimeout(() => {
      setError("");
      setSuccessMessage("");
    }, 8000);
  };

  return (
    <div className={styles.mainForm}>
      <form className={styles.formulario} onSubmit={handleSubmit}>
        <h2 className={styles.registroH2}>Regístrate</h2>
        <div className={styles.labelContainer}>
          <label>Nombre:</label>
          <input
            className={styles.inputs}
            placeholder="Ej. Diego"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>
        <div className={styles.labelContainer}>
          <label>Apellido:</label>
          <input
            className={styles.inputs}
            placeholder="Ej. De La Vega"
            type="text"
            value={lastname}
            onChange={(e) => setLastname(e.target.value)}
            required
          />
        </div>

        <div className={styles.labelContainer}>
          <label>Email:</label>
          <input
            className={styles.inputs}
            placeholder="Ej. diego@delavega.com"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div className={styles.labelContainer}>
          <label>Contraseña:</label>
          <input
            className={styles.inputs}
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit">Registrar</button>
      </form>

      <div className={styles.messages}>
        {error && <p className={styles.error}>{error}</p>}
        {successMessage && <p className={styles.success}>{successMessage}</p>}
      </div>
    </div>
  );
};

export default Registro;

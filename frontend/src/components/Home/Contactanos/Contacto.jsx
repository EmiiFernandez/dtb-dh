import React, { useState } from 'react';
import styles from '../Contactanos/Contacto.module.css';

const Contacto = () => {
  const [mostrarMensaje, setMostrarMensaje] = useState(false);
  const [formularioEnviado, setFormularioEnviado] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Aquí puedes agregar la lógica para enviar el formulario al servidor.
    // Si el envío es exitoso, puedes establecer formularioEnviado en true.
    // Por ahora, lo configuraremos como si el formulario se hubiera enviado correctamente.
    setFormularioEnviado(true);
  };

  return (
    <div className={styles.pantalla}>
      {!formularioEnviado ? (
        <form className={styles.formulario} onSubmit={handleSubmit}>
          <h1 className={styles.titulo}>Completá los Campos</h1>
          <div className={styles.campo}>
            <label><b>Nombre y Apellido</b></label>
            <input className={styles.input} type="text" name="nombre" required placeholder="Juan Perez" />
          </div>
          <div className={styles.campo}>
            <label><b>Correo electrónico</b></label>
            <input className={styles.input} type="email" name="mail" required placeholder="juanperez@mail.com" />
          </div>
          <div className={styles.campo}>
            <label><b>Comentanos con qué te podemos ayudar</b></label>
            <textarea className={styles.input} name="mensaje" id="mensaje" cols="60" rows="20" required placeholder="En qué podemos ayudarte?" />
          </div>
          <button className={styles.contactoButton} type="submit">Enviar</button>
        </form>
      ) : (
        <div className={styles.mensajeConfirmacion}>
          <p>Tu mensaje ha sido enviado correctamente; en breves alguien se pondra en contacto.</p>
        </div>
      )}
    </div>
  );
};

export default Contacto;




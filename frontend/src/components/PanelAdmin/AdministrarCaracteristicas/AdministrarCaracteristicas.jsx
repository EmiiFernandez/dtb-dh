import React, { useState } from "react";
import Select from "react-select";
import { FaAviato, FaRegSquare, FaToriiGate } from "react-icons/fa";
import styles from "./administrarCaracteristicas.module.css";

const iconOptions = [
  { value: "FaAviato", label: <FaAviato size={20} /> },
  { value: "FaRegSquare", label: <FaRegSquare size={20} /> },
  { value: "FaToriiGate", label: <FaToriiGate size={20} /> },
];

const AdministrarCaracteristicas = () => {
  const [caracteristicas, setCaracteristicas] = useState([]);
  const [nuevaCaracteristica, setNuevaCaracteristica] = useState("");
  const [nuevoIcono, setNuevoIcono] = useState("");
  const [caracteristicaEditando, setCaracteristicaEditando] = useState(null);

  const iconosDisponibles = {
    FaAviato: <FaAviato size={20} />,
    FaRegSquare: <FaRegSquare size={20} />,
    FaToriiGate: <FaToriiGate size={20} />,
  };

  const agregarCaracteristica = () => {
    if (nuevaCaracteristica && nuevoIcono) {
      setCaracteristicas([
        ...caracteristicas,
        { nombre: nuevaCaracteristica, icono: iconosDisponibles[nuevoIcono] },
      ]);
      setNuevaCaracteristica("");
      setNuevoIcono("");
    }
  };

  const handleEdit = (indice, nombre) => {
    setCaracteristicaEditando({ indice, nombre });
  };

  const cancelEdit = () => {
    setCaracteristicaEditando(null);
  };

  const saveEdit = (indice, nuevoNombre) => {
    const nuevasCaracteristicas = [...caracteristicas];
    nuevasCaracteristicas[indice].nombre = nuevoNombre;
    setCaracteristicas(nuevasCaracteristicas);
    setCaracteristicaEditando(null);
  };

  const handleDelete = (indice) => {
    const confirmDelete = window.confirm(
      "¿Estás seguro de que deseas eliminar esta característica?"
    );
    if (confirmDelete) {
      const nuevasCaracteristicas = caracteristicas.filter(
        (_, i) => i !== indice
      );
      setCaracteristicas(nuevasCaracteristicas);
    }
  };

  return (
    <div className={styles.administrarCaracteristicas}>
      <h2>Administrar Características</h2>

      <div className={styles.agregarCaracteristica}>
        <input
          type="text"
          placeholder="Nombre de la Característica"
          value={nuevaCaracteristica}
          onChange={(e) => setNuevaCaracteristica(e.target.value)}
        />
        <Select
          value={{ value: nuevoIcono, label: iconosDisponibles[nuevoIcono] }}
          options={iconOptions}
          onChange={(selectedOption) => setNuevoIcono(selectedOption.value)}
          placeholder="Selecciona un icono"
        />
        <button onClick={agregarCaracteristica}>Añadir Nueva</button>
      </div>

      <ul className={styles.listaCaracteristicas}>
        {caracteristicas.map((caracteristica, indice) => (
          <li key={indice}>
            {caracteristicaEditando?.indice === indice ? (
              <>
                <input
                  type="text"
                  value={caracteristicaEditando.nombre}
                  onChange={(e) =>
                    setCaracteristicaEditando({
                      ...caracteristicaEditando,
                      nombre: e.target.value,
                    })
                  }
                />

                {caracteristica.icono}

                <button
                  onClick={() =>
                    saveEdit(
                      indice,
                      caracteristicaEditando.nombre,
                      caracteristicaEditando.icono
                    )
                  }
                >
                  Guardar
                </button>
                <button onClick={cancelEdit}>Cancelar</button>
              </>
            ) : (
              <>
                <span>{caracteristica.nombre}</span>
                {caracteristica.icono}
              </>
            )}
            {caracteristicaEditando?.indice !== indice && (
              <>
                <button
                  onClick={() =>
                    handleEdit(
                      indice,
                      caracteristica.nombre,
                      caracteristica.icono
                    )
                  }
                >
                  Editar
                </button>
                <button onClick={() => handleDelete(indice)}>Eliminar</button>
              </>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AdministrarCaracteristicas;

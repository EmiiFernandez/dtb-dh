import React, { useState, useEffect } from 'react';
import { FaEdit, FaTrash } from 'react-icons/fa';
import styles from './administrarCategorias.module.css';

const AdministrarCategorias = () => {
  const [categorias, setCategorias] = useState([]);
  const [nuevaCategoria, setNuevaCategoria] = useState('');
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState(null);

  let token = sessionStorage.getItem("jwtToken")

  const fetchCategorias = async () => {
    try {
      const response = await fetch(`http://18.118.140.140/categories`);
      if (response.ok) {
        const categoriasData = await response.json();
        setCategorias(categoriasData);
      } else {
        console.error('Error al obtener las categorías');
      }
    } catch (error) {
      console.error('Error en la solicitud:', error);
    }
  };

  useEffect(() => {
    fetchCategorias();
  }, []);

  const handleCategoriaChange = (event) => {
    setNuevaCategoria(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const nuevaCategoriaData = {
      name: nuevaCategoria
    };

    try {
      const response = await fetch(`http://18.118.140.140/categories`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(nuevaCategoriaData)
      });

      if (response.ok) {
        console.log('Nueva categoría creada exitosamente');
        setNuevaCategoria('');
        fetchCategorias();
      } else {
        console.error('Error al crear la categoría');
      }
    } catch (error) {
      console.error('Error en la solicitud:', error);
    }
  };

  const handleEdit = (categoria) => {
    setCategoriaSeleccionada(categoria);
    setNuevaCategoria(categoria.name);
  };

  const handleDelete = async (categoriaId) => {
    const confirmDelete = window.confirm("¿Estás seguro de que deseas eliminar esta categoría?");
  
  if (confirmDelete) {
    try {
      const response = await fetch(`http://18.118.140.140/categories/${categoriaId}`, {
        method: 'DELETE',
        headers: {Authorization: `Bearer ${token}`}
      });

      if (response.ok) {
        console.log('Categoría eliminada exitosamente');
        fetchCategorias();
      } else {
        console.error('Error al eliminar la categoría');
      }
    } catch (error) {
      console.error('Error en la solicitud:', error);
    }}
  };

  const handleUpdateClick = async () => {
    const updatedCategoria = {
      id: categoriaSeleccionada.id,
      name: nuevaCategoria
    };

    try {
      const response = await fetch(`http://18.118.140.140/categories/${categoriaSeleccionada.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedCategoria)
      });

      if (response.ok) {
        console.log('Categoría actualizada exitosamente');
        setCategoriaSeleccionada(null);
        setNuevaCategoria('');
        fetchCategorias();
      } else {
        console.error('Error al actualizar la categoría');
      }
    } catch (error) {
      console.error('Error en la solicitud:', error);
    }
  };

  return  (
    <div className={styles.administrarCategorias}>
      <h2 className={styles.titulo}>Administrar Categorías</h2>
      <form className={styles.formulario} onSubmit={handleSubmit}>
      <div className={styles.inputContainer}>
        <label className={styles.etiqueta}>
          Nueva Categoría:
          <input
            className={styles.inputCategoria}
            type="text"
            value={nuevaCategoria}
            onChange={handleCategoriaChange}
          />
        </label>
        <button className={styles.boton} type="submit">Crear Categoría</button>
      </div>
      </form>
      <h3>Listado de Categorías</h3>
      <ul className={styles.categoriaLista}>
        {categorias.map((categoria) => (
          <li key={categoria.id} className={styles.categoriaItem}>
            <span className={styles.categoriaNombre}>{categoria.name}</span>
            <div className={styles.iconos}>
              <button
                className={styles.editarBoton}
                onClick={() => handleEdit(categoria)}
              >
                <FaEdit />
              </button>
              <button
                className={styles.eliminarBoton}
                onClick={() => handleDelete(categoria.id)}
              >
                <FaTrash />
              </button>
            </div>
          </li>
        ))}
      </ul>
      {categoriaSeleccionada && (
        <div>
          <h3>Editar Categoría</h3>
          <input
            className={styles.inputCategoria}
            type="text"
            value={nuevaCategoria}
            onChange={handleCategoriaChange}
          />
          <button className={styles.boton} onClick={handleUpdateClick}>Guardar Cambios</button>
        </div>
      )}
    </div>
  );
};

export default AdministrarCategorias;

import React, { useState } from 'react';
import AgregarProducto from './AgregarProducto/AgregarProducto';
import AgregarImagenes from './AgregarImagenes/AgregarImagenes';
import ListaProductos from './ListaProductos/ListaProductos';
import AdministrarCaracteristicas from './AdministrarCaracteristicas/AdministrarCaracteristicas';
import AdministrarCategorias from './AdministrarCategorias/AdministrarCategorias';
import styles from './panelAdministracion.module.css';
import { FaList, FaPlus, FaCog, FaTag, FaRegImages } from 'react-icons/fa';

const AdministracionPanel = () => {
  const [mostrarAgregarProducto, setMostrarAgregarProducto] = useState(false);
  const [mostrarAgregarImagenes, setMostrarAgregarImagenes] = useState(false)
  const [mostrarListaProductos, setMostrarListaProductos] = useState(false);
  const [mostrarAdminCaracteristicas, setMostrarAdminCaracteristicas] = useState(false);
  const [mostrarAdminCategorias, setMostrarAdminCategorias] = useState(false);

  const handleMostrarAgregarProducto = () => {
    setMostrarAgregarProducto(!mostrarAgregarProducto);
    setMostrarListaProductos(false);
    setMostrarAdminCaracteristicas(false);
    setMostrarAdminCategorias(false);
    setMostrarAgregarImagenes(false);
  };

  const handleMostrarAgregarImagenes = () => {
    setMostrarAgregarImagenes(!mostrarAgregarImagenes);
    setMostrarAgregarProducto(false);
    setMostrarListaProductos(false);
    setMostrarAdminCaracteristicas(false);
    setMostrarAdminCategorias(false); 
  };

  const handleMostrarListaProductos = () => {
    setMostrarAgregarProducto(false);
    setMostrarListaProductos(!mostrarListaProductos);
    setMostrarAdminCaracteristicas(false);
    setMostrarAdminCategorias(false);
    setMostrarAgregarImagenes(false);
  };

  const handleMostrarAdminCaracteristicas = () => {
    setMostrarAgregarProducto(false);
    setMostrarListaProductos(false);
    setMostrarAdminCaracteristicas(!mostrarAdminCaracteristicas);
    setMostrarAdminCategorias(false);
    setMostrarAgregarImagenes(false);
  };

  const handleMostrarAdminCategorias = () => {
    setMostrarAgregarProducto(false);
    setMostrarListaProductos(false);
    setMostrarAdminCaracteristicas(false);
    setMostrarAdminCategorias(!mostrarAdminCategorias);
    setMostrarAgregarImagenes(false);
  };

return (
  <div className={styles.adminContainer}>
    <div className={styles.title}>¿Qué cambios quieres realizar hoy?</div>
    <div className={styles.adminPanel}>
      <h1>Productos</h1>
      <nav className={styles.adminMenu}>
        <div className={styles.menuItem}>
          <div className={styles.menuItemIcon}>
            <FaPlus />
          </div>
          <p className={styles.descripcion}>Agrega un producto nuevo a tu catalogo</p>
          <button onClick={handleMostrarAgregarProducto}>Agregar Producto</button>
        </div>
        <div className={styles.menuItem}>
          <div className={styles.menuItemIcon}>
            <FaRegImages />
          </div>
          <p className={styles.descripcion}>Agrega imagenes a un producto</p>
          <button onClick={handleMostrarAgregarImagenes}>Agregar Imagenes</button>
        </div>
        <div className={styles.menuItem}>
          <div className={styles.menuItemIcon}>
            <FaList />
          </div>
          <p className={styles.descripcion}>Listado de tus productos, edita o elimina a tu preferencia</p>
          <button onClick={handleMostrarListaProductos}>Ver Lista</button>
        </div>
        <div className={styles.menuItem}>
          <div className={styles.menuItemIcon}>
            <FaTag />
          </div>
          <p className={styles.descripcion}>Crea nuevas categorias, edita o elimina las existentes</p>
          <button onClick={handleMostrarAdminCategorias}>Administrar Categorías</button>
        </div>
        <div className={styles.menuItem}>
          <div className={styles.menuItemIcon}>
            <FaCog />
          </div>
          <p className={styles.descripcion}>Crea nuevas caracteristicas, edita o elimina las existentess</p>
          <button onClick={handleMostrarAdminCaracteristicas}>Administrar Características</button>
        </div>
      </nav>

      {mostrarAgregarProducto && <AgregarProducto />} 
      {mostrarAgregarImagenes && <AgregarImagenes />} 
      {mostrarListaProductos && <ListaProductos />}
      {mostrarAdminCaracteristicas && <AdministrarCaracteristicas />}
      {mostrarAdminCategorias && <AdministrarCategorias />}
    </div>

    <div className={styles.adminCartel}>
      <p>Pantalla no disponible en dispositivo móvil</p>
    </div>
  </div>
);

};

export default AdministracionPanel;
import React, { useState, useEffect } from "react";
import styles from './favoritos.module.css'
import { MdFavorite } from "react-icons/md";

const Favoritos = () => {
  const [favoritos, setFavoritos] = useState([]);

  useEffect(() => {
    const storedFavoritos = JSON.parse(sessionStorage.getItem("favoritos")) || [];
    setFavoritos(storedFavoritos);
  }, []);

  const handleRemoveFavorito = (producto) => {
    // Filtra el producto seleccionado y actualiza la lista de favoritos
    const nuevosFavoritos = favoritos.filter((fav) => fav.id !== producto.id);
    setFavoritos(nuevosFavoritos);

    // Actualiza el sessionStorage
    sessionStorage.setItem("favoritos", JSON.stringify(nuevosFavoritos));
  };



  return (
    <div>
      <h2 className={styles.tituloLista}>Tus Favoritos</h2>
      <ul className={styles.cardProductos}>
        {favoritos.map((producto) => (
          <li className={styles.cardProductosLi} key={producto.id}>
            <button 
                onClick={() => handleRemoveFavorito(producto)}
                className={styles.favoritosButton}
            >
                <MdFavorite color="#4F709C" size={25} />
            </button>
            <img
                className={styles.imgLista}
                src={`http://18.118.140.140/s3/product-images/${producto.id}/0`}
                alt="imagenProducto"
            />
            <div className={styles.productoNombre}>{producto.name}</div>
            <div className={styles.productoPrecio}>$ {producto.price}</div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Favoritos;

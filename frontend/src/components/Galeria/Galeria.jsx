import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import styles from "./galeria.module.css";
import { BsFillArrowLeftCircleFill } from "react-icons/bs";

const GaleriaImg = () => {
  const { id } = useParams();
  console.log("/Galeria:", id);

  const [product, setProduct] = useState(null);
  const [images, setImages] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Obtener datos del producto
        const responseProduct = await fetch(`http://18.118.140.140/product/${id}`);
        const productJSON = await responseProduct.json();
        setProduct(productJSON);

        // Obtener datos de las imágenes
        const responseImages = await fetch(`http://18.118.140.140/s3/product-images/${id}`);
        const imagesJSON = await responseImages.json();
        
        // Construir las rutas de las imágenes
        const imagesArray = imagesJSON.map((_, index) => (
          `http://18.118.140.140/s3/product-images/${id}/${index}`
        ));
        
        setImages(imagesArray);
        
        console.log('PRODUCTOS DE LA API', productJSON);
        console.log('IMAGES DE LA API', imagesArray);
      } catch (error) {
        console.error('Error al obtener datos:', error);
      }
    };

    fetchData();
  }, [id]);

  return (
    <section className={styles.gallery}>
      <h1>Galería de Imágenes</h1>
      <div className={styles.galleryContainer}>
        {images.map((imagen, index) => (
          <img
            className={styles.imagenes}
            key={index}
            src={imagen}
            alt={`Imagen ${index + 1}`}
          />
        ))}
      </div>
      {/* Usar "id" en lugar de "pr" en la ruta */}
      <Link className={styles.aVolverDetalle} to={"/detalle/" + id}>
        <BsFillArrowLeftCircleFill color='#214F55' size={40}/>
      </Link>
    </section>
  );
};

export default GaleriaImg;


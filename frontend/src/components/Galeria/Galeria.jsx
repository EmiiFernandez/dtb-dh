import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import styles from "./galeria.module.css";
import { BsFillArrowLeftCircleFill } from "react-icons/bs";
import { fetchProductAndImages } from "../../utils/api/api";

const GaleriaImg = () => {
  const { id } = useParams();
  console.log("/Galeria:", id);

  const [product, setProduct] = useState(null);
  const [images, setImages] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      const { product, images } = await fetchProductAndImages(id);

      if (product && images.length > 0) {
        setProduct(product);
        setImages(images);
        console.log("PRODUCTOS DE LA APIIIIIIII", product);
        console.log("IMAGES DE LA APIIIIIIII", images);
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


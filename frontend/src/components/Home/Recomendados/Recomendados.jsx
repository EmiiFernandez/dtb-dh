import React, { useState, useEffect } from "react";
import styles from "./recomendados.module.css";
import { Link } from "react-router-dom";
import {
  BsFacebook,
  BsInstagram,
  BsTwitter,
  BsWhatsapp,
  BsShareFill,
} from "react-icons/bs";
import { MdFavoriteBorder, MdFavorite } from "react-icons/md";

const Recomendados = () => {
  const [isLoading, setIsLoading] = useState(true);
  const itemsPerPage = 10;
  const [productos, setProductos] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [randomizedData, setRandomizedData] = useState([]);
  const [favoritos, setFavoritos] = useState([]);
  const [isFavorito, setIsFavorito] = useState({});
  const [comment, setComment] = useState("");
  const [isAutenticado, setIsAutenticado] = useState(false);
  const [mostrarPopup, setMostrarPopup] = useState(false);

  async function fetchProductos() {
    const response = await fetch("http://18.118.140.140/product");
    const jsonData = await response.json();
    setProductos(jsonData);
    setIsLoading(false);
  }

  const checkAuthentication = () => {
    const isAutenticado = localStorage.getItem("jwtToken");
    setIsAutenticado(isAutenticado);
  };

  const openSharePopup = (product) => {
    const popup = document.getElementById(`popup${product.id}`);
    if (popup) {
      popup.style.display = "block";
    }
  };

  const closePopup = (product) => {
    const popup = document.getElementById(`popup${product.id}`);
    popup.style.display = "none";
  };

  useEffect(() => {
    // Mezcla el orden de los productos aleatoriamente
    const shuffledData = [...productos].sort(() => Math.random() - 0.5);
    setRandomizedData(shuffledData);
  }, [productos]);

  useEffect(() => {
    fetchProductos();
    checkAuthentication();
    const storedFavoritos = JSON.parse(localStorage.getItem("favoritos")) || [];
    if (Array.isArray(storedFavoritos)) {
      setFavoritos(storedFavoritos);
      const favoritosMap = {};
      storedFavoritos.forEach((favorito) => {
        favoritosMap[favorito.id] = true;
      });
      setIsFavorito(favoritosMap);
    }
  }, []);

  useEffect(() => {
    if (mostrarPopup) {
      const timer = setTimeout(() => {
        setMostrarPopup(false);
      }, 5000);
      return () => {
        clearTimeout(timer);
      };
    }
  }, [mostrarPopup]);

  const addToFavoritos = (producto) => {
    if (isAutenticado) {
      if (!favoritos.find((fav) => fav.id === producto.id)) {
        const nuevosFavoritos = [...favoritos, producto];
        setFavoritos(nuevosFavoritos);
        localStorage.setItem("favoritos", JSON.stringify(nuevosFavoritos));
        setIsFavorito({ ...isFavorito, [producto.id]: true });
      } else {
        const nuevosFavoritos = favoritos.filter(
          (fav) => fav.id !== producto.id
        );
        setFavoritos(nuevosFavoritos);
        localStorage.setItem("favoritos", JSON.stringify(nuevosFavoritos));
        setIsFavorito({ ...isFavorito, [producto.id]: false });
      }
    } else {
      setMostrarPopup(true);
    }
  };

  const totalPages = Math.ceil(productos.length / itemsPerPage);
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = randomizedData.slice(indexOfFirstItem, indexOfLastItem);

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  if (!productos) {
    return <p>Loading...</p>;
  }

  return (
    <div className={styles.contenedorPadre}>
      <span className={styles.title}>Lo que te recomendamos</span>

      <div className={styles.loginPop}>
        {mostrarPopup && (
          <div className={styles.loginPopup}>
            <p className={styles.loginPopupP}>
              Por favor, inicia sesión para marcar como favorito.
            </p>
          </div>
        )}
        <div className={styles.cardConteiner}>

          {isLoading ? 
            Array(10)
            .fill(null)
            .map((producto, index) => (
              <div className={styles.card} key={index}></div>
            )) : 
              currentItems.map((producto) => (
                <div key={producto.id} className={styles.card}>
                  <div className={styles.botonesFavshare}>
                    <button
                      onClick={() => addToFavoritos(producto)}
                      className={styles.favoritosButton}
                    >
                      {isFavorito[producto.id] ? (
                        <MdFavorite color="#4F709C" size={25} />
                      ) : (
                        <MdFavoriteBorder color="#4F709C" size={25} />
                      )}
                    </button>
                    <button
                      className={styles.buttonShare}
                      onClick={() => openSharePopup(producto)}
                    >
                      <BsShareFill color="#4F709C" size={19} />
                    </button>
                  </div>
    
                  <Link
                    className={styles.imgContainer}
                    key={producto.id}
                    to={"/detalle/" + producto.id}
                  >
                    <img
                      src={`http://18.118.140.140/s3/product-images/${producto.id}/0`}
                      alt="img-product-card"
                      className={styles.img}
                    />
                  </Link>
    
                  <div className={styles.dataContainer}>
                    <span className={styles.h3}>{producto.name}</span>
                    <br />
                    <span className={styles.h4}>{producto.price}</span>
                    <div className={styles.sharePopup} id={`popup${producto.id}`}>
                      <img
                        className={styles.sharePopupImg}
                        src={`http://18.118.140.140/s3/product-images/${producto.id}/0`}
                        alt="img-product-popup"
                      />
                      <p>{producto.name}</p>
                      <a
                        href={`/detalle/${producto.id}`}
                        className={styles.detailLink}
                      >
                        Ver mas
                      </a>
                      <input
                        className={styles.sharePopupInput}
                        type="text"
                        placeholder="Escribe tu comentario"
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                      />
                      <div className={styles.socialLinks}>
                        <a
                          className={styles.socialLinksA}
                          href={`https://www.facebook.com/share?url=http://g4-deploy-react-app.s3-website.us-east-2.amazonaws.com`}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          <BsFacebook color="#214F55" />
                        </a>
                        <a
                          className={styles.socialLinksA}
                          href={`https://www.instagram.com`}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          <BsInstagram color="#214F55" />
                        </a>
                        <a
                          className={styles.socialLinksA}
                          href={`https://twitter.com/share?url=http://g4-deploy-react-app.s3-website.us-east-2.amazonaws.com&text=${producto.name}`}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          <BsTwitter color="#214F55" />
                        </a>
                        <a
                          className={styles.socialLinksA}
                          href={`whatsapp://send?text=${encodeURIComponent(
                            `¡Mira este producto: ${producto.name}! http://g4-deploy-react-app.s3-website.us-east-2.amazonaws.com/detalle/${producto.id}`
                          )}`}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          <BsWhatsapp color="#214F55" />
                        </a>
                      </div>
    
                      <button
                        className={styles.closeButton}
                        onClick={() => closePopup(producto)}
                      >
                        Cerrar
                      </button>
                    </div>
                  </div>
                </div>
              ))}
        </div>
      </div>
      <div className={styles.pagination}>
        <button
          className={styles.buttonPagination}
          onClick={() => handlePageChange(1)}
          disabled={currentPage === 1}
        >
          {"<<"}
        </button>
        <button
          className={styles.buttonPagination}
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 1}
        >
          {"<"}
        </button>
        <span
          className={styles.numeracion}
        >{`${currentPage} / ${totalPages}`}</span>
        <button
          className={styles.buttonPagination}
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
        >
          {">"}
        </button>
        <button
          className={styles.buttonPagination}
          onClick={() => handlePageChange(totalPages)}
          disabled={currentPage === totalPages}
        >
          {">>"}
        </button>
      </div>
    </div>
  );
};

export default Recomendados;

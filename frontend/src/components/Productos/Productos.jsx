import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import styles from "./productos.module.css";
import {
  MdFavoriteBorder,
  MdFavorite,
  MdKeyboardArrowDown,
} from "react-icons/md";
import {
  BsFacebook,
  BsInstagram,
  BsTwitter,
  BsWhatsapp,
  BsShareFill,
} from "react-icons/bs";

const Productos = () => {
  const [isLoading, setIsLoading] = useState(true);
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [filtroCategorias, setFiltroCategorias] = useState([]);
  const [favoritos, setFavoritos] = useState([]);
  const [isFavorito, setIsFavorito] = useState({});
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [comment, setComment] = useState("");
  const [isAutenticado, setIsAutenticado] = useState(false);
  const [mostrarPopup, setMostrarPopup] = useState(false);

  const checkAuthentication = () => {
    const isAutenticado = sessionStorage.getItem("jwtToken");
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

  async function fetchProductos() {
    const response = await fetch("http://18.118.140.140/product");
    const jsonData = await response.json();
    setProductos(jsonData);
    setIsLoading(false);
  }

  async function fetchCategorias() {
    const response = await fetch("http://18.118.140.140/categories");
    const jsonData = await response.json();
    setCategorias(jsonData);
  }

  const handleCategoriaChange = (e) => {
    const checkbox = e.target;
    if (checkbox.checked) {
      setFiltroCategorias([...filtroCategorias, checkbox.value]);
    } else {
      const nuevosFiltros = filtroCategorias.filter(
        (categoria) => categoria !== checkbox.value
      );
      setFiltroCategorias(nuevosFiltros);
    }
  };

  useEffect(() => {
    setIsLoading(true);
    fetchProductos();
    fetchCategorias();
    checkAuthentication();
    const storedFavoritos =
      JSON.parse(sessionStorage.getItem("favoritos")) || [];
    setFavoritos(storedFavoritos);
    const favoritosMap = {};
    storedFavoritos.forEach((favorito) => {
      favoritosMap[favorito.id] = true;
    });
    setIsFavorito(favoritosMap);
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
        sessionStorage.setItem("favoritos", JSON.stringify(nuevosFavoritos));
        setIsFavorito({ ...isFavorito, [producto.id]: true });
      } else {
        const nuevosFavoritos = favoritos.filter(
          (fav) => fav.id !== producto.id
        );
        setFavoritos(nuevosFavoritos);
        sessionStorage.setItem("favoritos", JSON.stringify(nuevosFavoritos));
        setIsFavorito({ ...isFavorito, [producto.id]: false });
      }
    } else {
      setMostrarPopup(true);
    }
  };

  const productosFiltrados =
    filtroCategorias.length > 0 && filtroCategorias[0] != "*"
      ? productos.filter(
          (producto) =>
            filtroCategorias.indexOf(producto.category.id.toString()) != -1
        )
      : productos;

  console.log("productos filtrados", productosFiltrados);

  const toggleDropdown = () => {
    setDropdownOpen(!dropdownOpen);
  };

  return (
    <div className={styles.bodyProdutos}>
      <h2 className={styles.tituloLista}>Productos</h2>
      <div className={styles.filtroCategoria}>
        <div className={styles.selectWrapper}>
          <div className={styles.selectedItems} onClick={toggleDropdown}>
            <span>
              Categorias <MdKeyboardArrowDown color="whitesmoke" />
            </span>
            <i className={`fas fa-caret-${dropdownOpen ? "up" : "down"}`}></i>
          </div>
          {dropdownOpen && (
            <div className={styles.dropdown}>
              {categorias.map((categoria) => (
                <label key={categoria.id} className={styles.dropdownItem}>
                  <input
                    type="checkbox"
                    value={categoria.id}
                    checked={filtroCategorias.includes(categoria.id.toString())}
                    onChange={handleCategoriaChange}
                  />
                  {categoria.name}
                </label>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className={styles.listaProductosContainer}>
        <div className={styles.loginPop}>
          {mostrarPopup && (
            <div className={styles.loginPopup}>
              <p className={styles.loginPopupP}>
                Por favor, inicia sesión para marcar como favorito.
              </p>
            </div>
          )}
          <ul className={styles.cardProductos}>
            {isLoading &&
              Array(40)
                .fill(null)
                .map((producto, index) => (
                  <li className={styles.cardProductosLoading} key={index}></li>
                ))}
            {productosFiltrados.map((producto) => (
              <li className={styles.cardProductosLi} key={producto.id}>
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
                  className={styles.card}
                  key={producto.id}
                  to={"/detalle/" + producto.id}
                >
                  <img
                    className={styles.imgLista}
                    src={`http://18.118.140.140/s3/product-images/${producto.id}/0`}
                    alt="imagenProducto"
                  />
                </Link>
                <div className={styles.productoNombre}>{producto.name}</div>
                <div className={styles.productoPrecio}>${producto.price}</div>

                {/* Elementos para la ventana emergente de compartir */}
                <div className={styles.sharePopup} id={`popup${producto.id}`}>
                  <div className={styles.cardflex}>
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
                        href={`https://www.instagram.com/`}
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        <BsInstagram color="#214F55" />
                      </a>
                      <a
                        className={styles.socialLinksA}
                        href={`https://twitter.com/share?url=https://http://g4-deploy-react-app.s3-website.us-east-2.amazonaws.com&text=${producto.name}`}
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        <BsTwitter color="#214F55" />
                      </a>
                      <a
                        className={styles.socialLinksA}
                        href={`whatsapp://send?text=${encodeURIComponent(
                          `¡Mira este producto: ${producto.name}! http://g4-deploy-react-app.s3-website.us-east-2.amazonaws.com`
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
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default Productos;

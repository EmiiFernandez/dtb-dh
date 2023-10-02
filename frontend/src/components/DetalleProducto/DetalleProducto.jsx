import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { DateRangePicker } from "react-dates";
import "react-dates/initialize";
import "react-dates/lib/css/_datepicker.css";
import styles from "./detalleProducto.module.css";
import { BsFillArrowLeftCircleFill } from "react-icons/bs";
import { GiMusicalScore, GiMusicSpell } from "react-icons/gi";
import { FaStar, FaRegStar } from "react-icons/fa";
import { CgSearchLoading } from "react-icons/cg";
import moment from "moment";

const opcionesDePoliticas = [
  "Condiciones de entrega, horarios: Nuestra empresa ofrece entregas programadas de instrumentos musicales en horarios convenientes para nuestros clientes. Garantizamos la puntualidad y la integridad de los productos durante la entrega, asegurando que estén listos para su uso inmediato.",
  "Condiciones de devolución: Facilitamos el proceso de devolución de los instrumentos al finalizar el período de alquiler. Los clientes deben asegurarse de que los instrumentos estén en las mismas condiciones en las que fueron entregados para evitar cargos adicionales.",
  "Condiciones de uso: Los clientes son responsables de utilizar los instrumentos de manera apropiada y cuidadosa. Cualquier daño causado por un mal uso estará sujeto a cargos adicionales.",
  "Condiciones por producto dañado: En caso de daño accidental a un instrumento durante el período de alquiler, nuestros clientes deben notificarnos de inmediato. Se aplicarán tarifas de reparación o reemplazo según la magnitud del daño.",
  "Condición de producto perdido/robado (seguro): Ofrecemos opciones de seguro para proteger a nuestros clientes en caso de pérdida o robo de los instrumentos. Los detalles sobre las tarifas y coberturas se proporcionan al momento de la reserva.",
  "Condición de privacidad de datos: Respetamos la privacidad de nuestros clientes y sus datos personales. La información recopilada durante el proceso de reserva se utiliza únicamente con fines relacionados con el alquiler de instrumentos y se mantiene segura y confidencial.",
];

const opcionesAleatorias = shuffle(opcionesDePoliticas).slice(0, 2);

function shuffle(array) {
  let currentIndex = array.length,
    randomIndex,
    temporaryValue;

  while (currentIndex !== 0) {
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex--;

    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
}

const DetalleProducto = () => {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [images, setImages] = useState([]);
  const [imagenActual, setImagenActual] = useState(images[0]);
  const [agregarProducto, setAgregarProducto] = useState(false);
  const [focusedInput, setFocusedInput] = useState(null);
  const [rating, setRating] = useState(0);
  const [reviews, setReviews] = useState([]);
  const [currentReview, setCurrentReview] = useState("");
  const [dateRange, setDateRange] = useState({
    startDate: null,
    endDate: null,
  });

  let token = sessionStorage.getItem("jwtToken");
  let amount = 1;

  const [blockedDates, setBlockedDates] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      const responseProduct = await fetch(
        `http://18.118.140.140/product/${id}`
      );
      const productJSON = await responseProduct.json();
      const responseImages = await fetch(
        `http://18.118.140.140/s3/product-images/${id}`
      );
      const imagesJSON = await responseImages.json();
      const imagesArray = [];
      imagesJSON.forEach((_, index) => {
        imagesArray.push(
          `http://18.118.140.140/s3/product-images/${id}/${index}`
        );
      });
      setProduct(productJSON);
      setImages(imagesArray);
      console.log("PRODUCTOS DE LA APIIIIIIII", productJSON);
      console.log("IMAGES DE LA APIIIIIIII", imagesArray);
    };

    fetchData();
  }, [id]);

  //CALENDARIO FECHAS OCUPADAS (BLOQUEA LAS FECHAS)
  useEffect(() => {
    // Función para obtener las fechas ocupadas desde la API
    const fetchOccupiedDates = async () => {
      if (product && product.id) {
        try {
          const response = await fetch(
            `http://18.118.140.140/detail-booking/occupied-dates?productId=${product.id}`
          );
          if (response.ok) {
            const data = await response.json();
            // Formatea las fechas ocupadas en un formato compatible con react-dates
            const formattedDates = data.map((dateRange) => ({
              startDate: moment(dateRange.startDate),
              endDate: moment(dateRange.endDate).add(1, "day"), // Agregar 1 día al endDate
            }));
            setBlockedDates(formattedDates);
          } else {
            console.error("Error al obtener las fechas ocupadas");
          }
        } catch (error) {
          console.error("Error al realizar la solicitud:", error);
        }
      }
    };

    // Llama a la función para obtener las fechas ocupadas al cargar el componente
    fetchOccupiedDates();
  }, [product]);

  const calculateAverageRating = () => {
    if (reviews.length === 0) {
      return 0;
    }

    const totalRating = reviews.reduce((sum, review) => sum + review.rating, 0);
    return totalRating / reviews.length;
  };

  const averageRating = calculateAverageRating();

  const handleRatingChange = (newRating) => {
    setRating(newRating);
  };

  const handleReviewChange = (event) => {
    setCurrentReview(event.target.value);
  };

  const handleSubmitReview = () => {
    if (rating === 0) {
      alert("Por favor, seleccione una valoración antes de enviar la reseña.");
      return;
    }

    if (currentReview !== "") {
      const newReview = {
        rating,
        text: currentReview,
        date: moment().format("DD-MM-YYYY"),
      };
      setReviews([...reviews, newReview]);
      setCurrentReview("");
      setRating(0);
    }
  };

  if (!product) {
    return (
      <p>
        Cargando producto... <CgSearchLoading size={30} />
      </p>
    );
  }

  const cambiarImagen = (nuevaImagen) => {
    setImagenActual(nuevaImagen);
  };

  /// LO UTILIZA EL CALENDARIO PARA RESTRINGIR FECHAS PASADAS
  const isOutsideRange = (day) => {
    const today = moment();
    return day.isBefore(today, "day");
  };

  /// FORMATEA LAS FECHAS DEL CALENDARIO PARA OBTENER UN FORMATO YYYY-MM-DD
  const startDateAsMoment = dateRange.startDate;
  const startDateFormatted = startDateAsMoment
    ? startDateAsMoment.format("DD-MM-YYYY")
    : null;

  const endDateAsMoment = dateRange.endDate;
  const endDateFormatted = endDateAsMoment
    ? endDateAsMoment.format("DD-MM-YYYY")
    : null;

  /// FUNCION PARA HACER LA RESERVA HACIENDO POST AL SHOPPING-CART
  const addProducto = async () => {
    const bookProduct = {
      product: {
        id: product.id,
      },
      amount: amount,
      startBooking: startDateFormatted,
      endBooking: endDateFormatted,
    };
    try {
      if (startDateFormatted && endDateFormatted) {
        const response = await fetch("http://18.118.140.140/shopping-cart", {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(bookProduct),
        });

        if (response.ok) {
          alert(`Se ha agregado '${product.name}' a Reservas!`);
          setAgregarProducto(false);
        }
      } else {
        alert("Selecciona las fechas de incio y final antes de reservar.");
      }
    } catch (error) {
      console.error("Error al enviar la solicitud:", error);
      alert("Error al enviar la solicitud");
    }
  };

  const isDayBlocked = (day) => {
    // Comprueba si la fecha está dentro del rango de fechas ocupadas
    return blockedDates.some((blockedDate) =>
      day.isBetween(blockedDate.startDate, blockedDate.endDate, null, "[]")
    );
  };

  return (
    <div className={styles.detalleProducto}>
      <div key={product.id}>
        <section className={styles.detalleHeader}>
          <div className={styles.caracteristicas}>
            <div className={styles.caracteristicasIndiv}>
              <b>
                <GiMusicalScore />
                Categoria:
              </b>
              <p>{product.category.name}</p>
            </div>
            <div className={styles.caracteristicasIndiv}>
              <b>
                <GiMusicSpell />
                Marca:
              </b>
              <p>{product.brand.name}</p>
            </div>
          </div>
          <Link className={styles.flecha} to="/">
            <BsFillArrowLeftCircleFill color="#214F55" size={40} />
          </Link>
        </section>

        <section className={styles.detalleBody}>
          <article className={styles.ladoIzquierdo}>
            <h3 className={styles.h3}>{product.name}</h3>
            <p className={styles.productDescription}>{product.description}</p>
            <div className={styles.precioBoton}>
              <p className={styles.precio}>$ {product.price}</p>
              <button
                className={styles.botonReserva}
                onClick={() => {
                  addProducto();
                }}
              >
                Reservar
              </button>
            </div>
            <div className={styles.calendario}>
              <h4>Selecciona un rango de fechas</h4>
              <DateRangePicker
                displayFormat="DD-MM-YYYY"
                startDate={dateRange.startDate}
                startDateId="start_date_id"
                endDate={dateRange.endDate}
                endDateId="end_date_id"
                onDatesChange={({ startDate, endDate }) => {
                  setDateRange({ startDate, endDate });
                }}
                focusedInput={focusedInput}
                onFocusChange={(focusedInput) => setFocusedInput(focusedInput)}
                isOutsideRange={isOutsideRange}
                isDayBlocked={isDayBlocked}
              />
            </div>
          </article>

          <article className={styles.ladoDerecho}>
            <div className={styles.imgContainer}>
              {imagenActual ? (
                <div className={styles.productImageBox}>
                  <img
                    src={imagenActual}
                    alt="img-product"
                    className={styles.productImage}
                  />
                </div>
              ) : (
                <div className={styles.errorBox}>
                  {images[0] && (
                    <img
                      src={images[0]}
                      alt="img-product"
                      className={styles.productImage}
                      onClick={() => cambiarImagen(images[0])}
                    />
                  )}
                </div>
              )}
              <div className={styles.product4}>
                <div className={styles.product2}>
                  {images.slice(0, 2).map((image, index) => (
                    <img
                      key={index}
                      src={image}
                      alt={`img-product-${index}`}
                      className={styles.productImg}
                      onClick={() => cambiarImagen(image)}
                    />
                  ))}
                </div>
                <div className={styles.product2}>
                  {images.slice(2, 4).map((image, index) => (
                    <img
                      key={index}
                      src={image}
                      alt={`img-product-${index + 2}`}
                      className={styles.productImg}
                      onClick={() => cambiarImagen(image)}
                    />
                  ))}
                </div>
              </div>
            </div>
            <button className={styles.VerMasBox}>
              <Link className={styles.a} to={`/Galeria/${id}`}>
                Ver Más
              </Link>
            </button>
          </article>
        </section>

        <section className={styles.reviewContainer}>
          <div className={styles.reviewIzquierda}>
            <div className={styles.averageRating}>
              <div className={styles.ratingStars}>
                {[1, 2, 3, 4, 5].map((star) => (
                  <span
                    key={star}
                    className={`${
                      star <= averageRating
                        ? styles.starActive
                        : styles.starInactive
                    }`}
                  >
                    <FaStar />
                  </span>
                ))}
              </div>
              <span className={styles.averageRatingNumber}>
                {averageRating.toFixed(1)}
              </span>
              <p className={styles.reviewCount}>
                ({reviews.length} valoraciones)
              </p>
            </div>
          </div>
          <div className={styles.reviewDerecha}>
            <div className={styles.reviewSection}>
              <h4>Puntuación</h4>
              <div className={styles.ratingStars}>
                {[1, 2, 3, 4, 5].map((star) => (
                  <span
                    key={star}
                    className={`${
                      star <= rating ? styles.starActive : styles.starInactive
                    }`}
                    onClick={() => handleRatingChange(star)}
                  >
                    {star <= rating ? <FaStar /> : <FaRegStar />}
                  </span>
                ))}
              </div>
              <textarea
                className={styles.reviewTextArea}
                placeholder="Escribe tu reseña aquí"
                value={currentReview}
                onChange={handleReviewChange}
              ></textarea>
              <button
                className={styles.submitReviewButton}
                onClick={handleSubmitReview}
              >
                Enviar Reseña
              </button>
            </div>

            <div className={styles.reviewsUsers}>
              <h4 className={styles.reviewsTitle}>Reseñas</h4>
              <ul className={styles.reviewsList}>
                {reviews.map((review, index) => (
                  <li className={styles.reviewItem} key={index}>
                    <div className={styles.reviewHeader}>
                      <p className={styles.reviewDate}>{review.date}</p>
                      <div className={styles.reviewRating}>
                        <span className={styles.starActive}>
                          {Array.from({ length: review.rating }, (_, index) => (
                            <FaStar key={index} />
                          ))}
                          {Array.from(
                            { length: 5 - review.rating },
                            (_, index) => (
                              <FaRegStar key={index} />
                            )
                          )}
                        </span>
                      </div>
                    </div>
                    <p className={styles.reviewText}>{review.text}</p>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </section>
        <section className={styles.politicasContainer}>
          <h4>Políticas</h4>
          <div className={styles.tarjetasContainer}>
            {opcionesAleatorias.map((opcion, index) => (
              <div className={styles.tarjeta} key={index}>
                {opcion}
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
};

export default DetalleProducto;
import React, { useState, useEffect } from "react";
import { DateRangePicker, isSameDay } from "react-dates";
import "react-dates/initialize";
import "react-dates/lib/css/_datepicker.css";
import styles from "../Buscador/buscador.module.css";
import { BsBookmarkCheck } from "react-icons/bs";
import moment from "moment";

function Buscador() {
  const [dateRange, setDateRange] = useState({
    startDate: null,
    endDate: null,
  });
  const [focusedInput, setFocusedInput] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [productos, setProductos] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [autoCompleteSuggestions, setAutoCompleteSuggestions] = useState([]);
  const [blockedDates, setBlockedDates] = useState([]);

  let token = sessionStorage.getItem("jwtToken");
  let amount = 1;

  /// FORMATEA LAS FECHAS DEL CALENDARIO PARA OBTENER UN FORMATO YYYY-MM-DD
  const startDateAsMoment = dateRange.startDate;
  const startDateFormatted = startDateAsMoment
    ? startDateAsMoment.format("DD-MM-YYYY")
    : null;

  const endDateAsMoment = dateRange.endDate;
  const endDateFormatted = endDateAsMoment
    ? endDateAsMoment.format("DD-MM-YYYY")
    : null;

  const startDateForBackend = startDateAsMoment
    ? startDateAsMoment.format("YYYY-MM-DD")
    : null;

  const endDateForBackend = endDateAsMoment
    ? endDateAsMoment.format("YYYY-MM-DD")
    : null;
  //YYYY-MM-DD ENVIO DE INFO A LA BASE DE DATOS - DD-MM-YYYY INFO ENVIADA AL FRONT PARA VISTA USUARIO
  //FIN FORMATEO DE FECHAS

  //BUSCADOR
  const handleSearchInputChange = (event) => {
    const value = event.target.value;
    setSearchKeyword(value);
    // Realizar una solicitud para obtener la lista de productos actualizada
    fetch(`http://18.118.140.140/product`)
      .then((response) => response.json())
      .then((data) => {
        setProductos(data);
      })
      .catch((error) =>
        console.error("Error al realizar la solicitud de búsqueda:", error)
      );
      

    // Verificar si la longitud de la cadena de búsqueda es mayor o igual a 3
    if (value.length >= 1) {
      // Filtrar sugerencias basadas en el valor de entrada
      const filteredSuggestions = productos.filter((product) =>
        product.name.toLowerCase().includes(value.toLowerCase())
      );

      setAutoCompleteSuggestions(filteredSuggestions);

    } else {
      // Si la longitud es menor a 3, limpiar las sugerencias
      setAutoCompleteSuggestions([]);
    }
  };
  const handleAutoCompleteSelection = (suggestion) => {
    setSelectedProduct(suggestion); // Almacena el producto seleccionado
    setSearchKeyword(suggestion.name); // Llena el input de búsqueda con el nombre del producto seleccionado
    setAutoCompleteSuggestions([]); // Limpia las sugerencias
  };

  //BOTON BUSCAR RESERVA POR PRODUCTO SELECCIONADO Y FECHAS
  const handleBuscar = () => {
    if (searchKeyword.length >= 3) {
      fetch(`http://18.118.140.140/product/search?keyword=${searchKeyword}`)
        .then((response) => response.json())
        .then((data) => {
          setSearchResults(data);
          setSearchKeyword("");
        })
        .catch((error) =>
          console.error("Error al realizar la solicitud:", error)
        );
    } else {
      setSearchResults([]);
    }
  };

  /// FUNCION PARA HACER LA RESERVA HACIENDO POST AL SHOPPING-CART

  //CHEQUEA QUE TENGA TOKEN
  const isUserLoggedIn = () => {
    if (token) {
      return true; // El usuario está logueado y el token es válido
    }
    return false; // El usuario no está logueado o el token es inválido
  };

  //BOTON RESERVAR PRODUCTO (SI EL USUARIO NO ESTA LOGUEADO SALE UN ALERT)
  const handleReservarProducto = async () => {
    if (isUserLoggedIn()) {
      try {
        const bookProduct = {
          product: {
            id: selectedProduct.id,
          },
          amount: amount,
          startBooking: startDateForBackend,
          endBooking: endDateForBackend,
        };

        const response = await fetch("http://18.118.140.140/shopping-cart", {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(bookProduct),
        });

        if (response.ok) {
          setProductos(...productos, bookProduct);
          alert(`Se ha agregado '${selectedProduct.name}' a Reservas!`);
        } else {
          console.error("Error al enviar la solicitud:", response.statusText);
        }
      } catch (error) {
        console.error("Error al enviar la solicitud:", error);
        alert("Error al realizar la reserva.");
      }
    } else {
      // El usuario no está logueado, muestra un alert
      alert("Debe iniciar sesión para realizar la reserva.");
    }
  };
  //TERMINA EL BOTON DE RESERVAR

  /// FUNCION PARA RESTRINGIR FECHAS PASADAS
  const isOutsideRange = (day) => {
    const today = moment();
    return day.isBefore(today, "day");
  };

  //CALENDARIO FECHAS OCUPADAS (BLOQUEA LAS FECHAS)
  useEffect(() => {
    // Función para obtener las fechas ocupadas desde la API
    const fetchOccupiedDates = async () => {
      if (selectedProduct && selectedProduct.id) {
        try {
          const response = await fetch(
            `http://18.118.140.140/detail-booking/occupied-dates?productId=${selectedProduct.id}`
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
  }, [selectedProduct]);

  const isDayBlocked = (day) => {
    // Comprueba si la fecha está dentro del rango de fechas ocupadas
    return blockedDates.some((blockedDate) =>
      day.isBetween(blockedDate.startDate, blockedDate.endDate, null, "[]")
    );
  };
  //TERMINA LOGICA CALENDARIO FECHAS OCUPADAS (BLOQUEA LAS FECHAS)

  return (
    <div className={styles.buscadorContainer1}>
      <h3 className={styles.buscadorH3}>
        ¿Necesitas ese producto en específico? Búscalo aquí
      </h3>

      <input
        className={styles.inputBuscador}
        type="text"
        placeholder="Ingrese un producto"
        value={searchKeyword}
        onChange={handleSearchInputChange}
      />

      {autoCompleteSuggestions.length > 0 && (
        <ul className={styles.autoCompleteList}>
          {autoCompleteSuggestions.map((suggestion) => (
            <li key={suggestion.id}>
              <button
                className={styles.btnAutocomplete}
                onClick={() => handleAutoCompleteSelection(suggestion)}
              >
                {suggestion.name}
              </button>
            </li>
          ))}
        </ul>
      )}

      <div className={styles.datePicker}>
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
          isDayBlocked={isDayBlocked} // Utiliza la función para bloquear fechas ocupadas
        />
      </div>

      <button className={styles.buscadorButton} onClick={handleBuscar}>
        Realizar Búsqueda
      </button>

      <ul className={styles.inputUl}>
        {searchResults.map((product) => (
          <li className={styles.inputLi} key={product.id}>
            {/* Renderizar los productos encontrados en tarjetas */}

            <div key={product.id} className={styles.productoCard}>
              <h4 className={styles.productoCardH4}>{product.name}</h4>
              <p className={styles.productoCardP}>
                Fecha de inicio: {startDateFormatted}
              </p>
              <p className={styles.productoCardP}>
                Fecha de fin: {endDateFormatted}
              </p>
              <button
                className={styles.productoCardButton}
                onClick={handleReservarProducto}
              >
                <BsBookmarkCheck size={18} color="whitesmoke" />
              </button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Buscador;

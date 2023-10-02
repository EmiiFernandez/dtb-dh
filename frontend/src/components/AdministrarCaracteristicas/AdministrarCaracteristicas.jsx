import React, { useState, useEffect, useMemo, useRef } from 'react';
import styles from './administrarCaracteristicas.module.css';

function AdministrarCaracteristicas() {
  const [products, setProducts] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [selectedFeatures, setSelectedFeatures] = useState([]);
  const [allFeatures, setAllFeatures] = useState([]);
  const [featuresElegidas, setFeaturesElegidas] = useState([]);
  // Uso useMemo para que no se renderice todo el tiempo, solo cuando tenga un cambio
  const featuresArray = useMemo(() => featuresElegidas.map(feature => ({ id: `${feature}` })), [featuresElegidas]);
  const [invocarModificacion, setInvocarModificacion] = useState(false);
  const selectedProductRef = useRef(null);

  const token = sessionStorage.getItem("jwtToken");

  useEffect(() => {
    // Obtener la lista de productos
    fetch("http://18.118.140.140/product")
      .then((response) => response.json())
      .then((data) => {
        setProducts(data);
      })
      .catch((error) => {
        console.error('Error al obtener la lista de productos:', error);
      });

    // Obtener la lista de características
    fetch("http://18.118.140.140/features")
      .then((response) => response.json())
      .then((data) => {
        setAllFeatures(data);
      })
      .catch((error) => {
        console.error('Error al obtener la lista de características:', error);
      });
  }, []);

  const handleProductClick = (product) => {
    setSelectedProduct(product);
    if (selectedProductRef.current) {
      selectedProductRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };
  
  const handleFeatureClick = (featureId) => {
 
    if (selectedFeatures.includes(featureId)) {
      const updatedSelectedFeatures = selectedFeatures.filter(id => id !== featureId);
      setSelectedFeatures(updatedSelectedFeatures);
      setFeaturesElegidas(updatedSelectedFeatures)
    } else {
      setSelectedFeatures([...selectedFeatures, featureId]);
    }
  };

  useEffect(() => {
    const modificarFeature = async () => {
      const requestBody = {
        features: selectedFeatures.map(id => ({ id: id })),
      };

      try {
        const response = await fetch(`http://18.118.140.140/product/${selectedProduct.id}`, {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(requestBody),
        });

        if (response.ok) {
          console.log("Producto actualizado con éxito");
          const updatedProducts = products.map((product) =>
            product.id === selectedProduct.id ? selectedProduct : product
          );

          setProducts(updatedProducts);
          setSelectedProduct(selectedProduct);
          alert(`Producto '${selectedProduct.name}' actualizado exitosamente!`);

            // Recargar la página
            window.location.reload();

        } else {
          console.error('Error en la respuesta del servidor:', response.status);
          alert('Hubo un error al actualizar el producto.');
        }
      } catch (error) {
        console.error('Error al enviar la solicitud:', error);
        alert('Hubo un error al crear el producto.');
      }
    };

    if (invocarModificacion) {
      modificarFeature();
      setInvocarModificacion(false);
    }
  }, [invocarModificacion]);

  // Imprimir las selecciones en la consola
  useEffect(() => {
    console.log("Producto seleccionado:", selectedProduct);
    console.log("Características seleccionadas:", selectedFeatures);
  }, [selectedProduct, selectedFeatures]);

  return (
    <div>
      <h2 className={styles.heading}>Edita las caracteristicas de tus Productos</h2>
      <div className={styles.card}>
        <table className={styles.listaProductos}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Imagen</th>
              <th>Nombre</th>
              <th>Características</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr
                key={product.id}
                onClick={() => handleProductClick(product)}
                className={product === selectedProduct ? styles.selectedRow : ''}
              >
                <td>{product.id}</td>
                <td>
                  <img
                    src={`http://18.118.140.140/s3/product-images/${product.id}/0`}
                    alt={product.name}
                    className={styles.productImage}
                  />
                </td>
                <td>{product.name}</td>
                <td>
                  {product.features.map((feature, index) => (
                    <span key={index}>{feature.name}, </span>
                  ))}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {selectedProduct && (
        <div ref={selectedProductRef} className={styles.featureCard}>
          <h3 className={styles.subheading}>Características de {selectedProduct.name}</h3>
          <form>
            {allFeatures.map((feature) => (
              <div key={feature.id}>
                <label>
                  <input
                    type="checkbox"
                    value={feature.id}
                    checked={selectedFeatures.includes(feature.id)}
                    onChange={() => {
                      handleFeatureClick(feature.id);
                    }}
                  />
                  {feature.name}
                </label>
              </div>
            ))}
          </form>
          <button
            className={styles.featureCard.button}
            onClick={() => setInvocarModificacion(true)}
          >
            Aplicar Cambios
          </button>
        </div>
      )}
    </div>
  );
}

export default AdministrarCaracteristicas;
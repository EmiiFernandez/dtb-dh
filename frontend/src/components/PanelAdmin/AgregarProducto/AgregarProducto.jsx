import React, { useState, useEffect } from 'react';
import styles from './agregarProducto.module.css';

function AgregarProductos() {
  const [name, setName] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [price, setPrice] = useState(0);
  const [categoria, setCategoria] = useState('');
  const [categorias, setCategorias] = useState([]);
  const [brands, setBrands] = useState([]);
  const [brand, setBrand] = useState('');
  const [features, setFeatures] = useState([]);
  const [featuresElegidas, setFeaturesElegidas] = useState([]);
  const featuresArray = featuresElegidas.map(feature => ({ id: `${feature}`}));
  const [success, setSuccess] = useState('');
  const [errorPost, setErrorPost] = useState('');
  const [errorName, setErrorName] = useState('');
  const [errorDescripcion, setErrorDescripcion] = useState('');
  const [errorPrice, setErrorPrice] = useState('');
  const [errorCategoria, setErrorCategoria] = useState('');
  const [errorMarca, setErrorMarca] = useState('');
  const [errorFeatures, setErrorFeatures] = useState('');


  let stock = '3';
  let token = sessionStorage.getItem("jwtToken")

  useEffect(() => {
    // Obtener categorías usando fetch
    fetch("http://18.118.140.140/categories")
      .then(response => response.json())
      .then((data) => {
        data.sort((a,b) =>
        a.name.localeCompare(b.name));
        setCategorias(data)
      })
      .catch(error => console.error('Error al obtener categorías:', error));

    // Obtener caracteristicas usando fetch
    fetch("http://18.118.140.140/brand")
      .then(response => response.json())
      .then((data) => {
        data.sort((a,b) =>
        a.name.localeCompare(b.name));
        setBrands(data)
      })
      .catch(error => console.error('Error al obtener marcas:', error));
  }, []);

  useEffect(() => {
    fetch("http://18.118.140.140/features")
      .then(response => response.json())
      .then((data) => {
        data.sort((a,b) =>
        a.name.localeCompare(b.name));
        setFeatures(data)
      })
      .catch(error => console.error('Error al obtener categorías:', error));
  }, []);


  async function crearProducto() {
    const product = {
      name: name,
      description: descripcion,
      price: price,
      stock: stock,
      brand: {
        "id": brand
      },
      category: {
        "id": categoria
      },
      features: featuresArray
    }
    try {
      const response = await fetch("http://18.118.140.140/product", {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body:JSON.stringify(product)
      });
      if (response.ok) {
          setSuccess(`El producto ${product.name} se ha creado exitosamente`)
      } else {
        setErrorPost('Hubo un error al crear el producto');
      }
    } catch (error) {
      console.error('Error al enviar la solicitud:', error);
    }
  };


  const toggleFeatureElegidas = (featureId) => {
    if (featuresElegidas.includes(featureId)) {
      setFeaturesElegidas(featuresElegidas.filter(item => item !== featureId));
    } else {
      setFeaturesElegidas([...featuresElegidas, featureId]);
    }
  };

  function cleanInputs() {
    setName('');
    setDescripcion('');
    setPrice(0);
    setBrand('');
    setCategoria('');
    setFeaturesElegidas([]);
  }


  function handleSubmitProducto(e) {
    e.preventDefault();
    //validaciones
    let hasSomeError = false
    if (name.length < 3) {
      setErrorName('La longitud del nombre debe ser mayor a 3 caracteres')
      hasSomeError = true
    } else {
      setErrorName()
    }

    if (descripcion.length < 30 || descripcion > 200) {
      setErrorDescripcion('La longitud de la descripcion debe ser mayor a 30 caracteres y no más de 200')
      hasSomeError = true
    } else {
      setDescripcion()
    }

    if (price <= 0 || price < 50) {
      setErrorPrice('El precio debe ser mayor a $50')
      hasSomeError = true
    } else {
      setErrorPrice()
    }

    if (categoria === '') {
      setErrorCategoria('Debes elegir una categoría')
      hasSomeError = true
    } else {
      setErrorCategoria()
    }

    if (brand === '') {
      setErrorMarca('Debes elegir una marca')
      hasSomeError = true
    } else {
      setErrorMarca()
    }

    if (featuresElegidas.length < 1) {
      setErrorFeatures('Debes elegir al menos una característica')
      hasSomeError = true
    } else {
      setErrorFeatures()
    }

    if (hasSomeError) {
      cleanInputs();
      return 
    }

    crearProducto()
    cleanInputs();
  }
  
  return (
    <>
      <form onSubmit={handleSubmitProducto} className={styles.agregarProducto}>
        <h1>Agregar Productos</h1>
        <div>
          <label>Nombre:</label>
          <input type="text" value={name} onChange={e => setName(e.target.value)} />
          {errorName ? 
            <span
              style={{
                color: 'red',
                fontWeight: 'bold'
              }}
            >
              {errorName}
            </span> 
          : undefined}
        </div>
        <div>
          <label>Descripción:</label>
          <input type="text" value={descripcion} onChange={e => setDescripcion(e.target.value)} />
          {errorDescripcion ? 
            <span 
              style={{
                color: 'red',
                fontWeight: 'bold'
              }}
            >
                {errorDescripcion}
            </span> 
          : undefined}
        </div>
        <div>
          <label>Precio:</label>
          <input type="number" value={price} onChange={e => setPrice(e.target.value)} />
          {errorPrice ? 
            <span
              style={{
                color: 'red',
                fontWeight: 'bold'
              }}
            >
              {errorPrice}
            </span> 
          : undefined}
        </div>
        <div>
          <label>Categoría:</label>
          <select value={categoria} onChange={e => setCategoria(e.target.value)}>
            <option value="" disabled selected>Selecciona una categoría</option>
            {categorias.map((cat) => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
          {errorCategoria ? 
            <span
              style={{
                color: 'red',
                fontWeight: 'bold'
              }}
            >
              {errorCategoria}
            </span> 
          : undefined}
        </div>
        <div>
          <label>Marcas:</label>
          <select value={brand} onChange={e => setBrand(e.target.value)}>
            <option value="" disabled selected>Selecciona una marca</option>
            {brands.map((brand) => (
              <option key={brand.id} value={brand.id}>{brand.name}</option>
            ))}
          </select>
          {errorMarca ? 
            <span
              style={{
                color: 'red',
                fontWeight: 'bold'
              }}
            >
              {errorMarca}
            </span> 
            : undefined}
        </div>
        <div className={styles.featuresFather}>
          <label>Características:</label>
          {features.map((feature) => (
            <div className={styles.features} key={feature.id}>
              <span>{feature.name}</span>
              <input
                type="checkbox"
                value={feature.name}
                name={`feature-${feature.id}`}
                checked={featuresElegidas.includes(feature.id)}
                onChange={() => toggleFeatureElegidas(feature.id)}
              />
            </div>
          ))}
          {errorFeatures ? 
            <span
              style={{
                color: 'red',
                fontWeight: 'bold'
              }}
            >
              {errorFeatures}
            </span> 
            : undefined}
        </div>
        <button type='submit'>Crear Producto</button>
      </form>

      {errorPost ? <span className={styles.errorForm}>{errorPost}</span> : undefined}
      {success ? <span className={styles.success}>{success}</span> : undefined}
    </>
  );
}

export default AgregarProductos;

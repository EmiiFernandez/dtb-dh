import styles from "./agregarImagenes.module.css";
import { useState, useEffect } from "react";

function AgregarImagenes() {
    const [errorForm, setErrorForm] = useState('');
    const [success, setSuccess] = useState('');
    const [imagenes, setImagenes] = useState([]);
    const [productos, setProductos] = useState([]);
    const [producto, setProducto] = useState('');
    let token = sessionStorage.getItem('jwtToken')

    
    async function handleProductos() {
        const response = await(await
            fetch('http://18.118.140.140/product')
            ).json()
            .then((data) => {
                data.sort((a,b) =>
                a.name.localeCompare(b.name));
                setProductos(data)
              })
    }

    async function postImagenes(id, formData) {
        try {
            const response = await fetch(`http://18.118.140.140/s3/product-images/${id}`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    body: formData
                });
                if (response.ok) {
                    setSuccess("Imagen agregada con éxito")
                }
        } catch (error) {
            console.error('Error al subir imagenes', error)
        }
    }

    useEffect(() => {
       handleProductos()
      }, []);
    

    const handleImagenesChange = (e) => {
        const files = Array.from(e.target.files);
        setImagenes(files);
    };

    function handleSubmitImagenes(e) {
        e.preventDefault();
        if (producto === "" || imagenes.length == 0) {
            setErrorForm("Recuerda de completar los campos")
        } else {
            console.log("Sigo subiendo");
            let data = new FormData();
            imagenes.forEach((imagen) => data.append('file', imagen));
            postImagenes(producto, data);
        }
    }
    return(
        <>
            <form onSubmit={handleSubmitImagenes} className={styles.agregarImagenes}>
                <h1>Agrega Imagenes</h1>

                <section>
                    <span>Producto: </span>
                    <select value={producto} onChange={e => setProducto(e.target.value)} className={styles.allProducts}>
                        <option value={""} disabled selected>Selecciona un producto</option>
                        {productos.map((producto) => (
                        <option key={producto.id} value={producto.id}>{producto.name}</option>
                        ))}
                    </select>
                </section>
                
                <input id="images" type="file" onChange={handleImagenesChange} className={styles.imagenes}/>

            
                <button type="submit">Subir imagenes</button>
            </form>
            
            {errorForm ? <span className={styles.errorForm}>{errorForm}</span> : undefined}
            {success ? <span className={styles.success}>{success}</span> : undefined}
        </>
    )
}

export default AgregarImagenes;
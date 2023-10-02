import React, { useState, useEffect } from "react";
import styles from "./listaProductos.module.css";
import { FaTrash, FaSave, FaEdit } from "react-icons/fa";

let token = sessionStorage.getItem("jwtToken");

const ListaProductos = () => {
  const [productos, setProductos] = useState([]);
  const [editingProduct, setEditingProduct] = useState(null);
  const [editName, setEditName] = useState("");
  const [editDescription, setEditDescription] = useState("");
  const [selectedBrand, setSelectedBrand] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const [editPrice, setEditPrice] = useState("");
  const [availableBrands, setAvailableBrands] = useState([]);
  const [availableCategories, setAvailableCategories] = useState([]);
  const [editedData, setEditedData] = useState({});

  useEffect(() => {
    // Obtener las opciones de marcas desde el servidor
    fetch("http://18.118.140.140/brand")
      .then((response) => response.json())
      .then((data) => {
        setAvailableBrands(data);
      })
      .catch((error) => console.log("Error fetching brands:", error));

    // Obtener las opciones de categorías desde el servidor
    fetch("http://18.118.140.140/categories")
      .then((response) => response.json())
      .then((data) => {
        setAvailableCategories(data);
      })
      .catch((error) => console.log("Error fetching categories:", error));

    // Obtener la lista de productos desde el servidor
    fetch("http://18.118.140.140/product")
      .then((response) => response.json())
      .then((data) => {
        setProductos(data);
      })
      .catch((error) => console.log("Error fetching data:", error));
  }, []);

  const handleDelete = (id) => {
    const confirmDelete = window.confirm(
      "¿Estás seguro de que deseas eliminar este producto?"
    );
    if (confirmDelete) {
      fetch(`http://18.118.140.140/product/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      })
        .then((response) => {
          if (response.ok) {
            const updatedProductos = productos.filter(
              (producto) => producto.id !== id
            );
            setProductos(updatedProductos);
            console.log("Producto eliminado exitosamente");
          } else {
            console.error("Error al eliminar el producto");
          }
        })
        .catch((error) => {
          console.error("Error al realizar la solicitud:", error);
        });
    }
  };

  const handleEdit = (id) => {
    const productToEdit = productos.find((producto) => producto.id === id);

    setEditingProduct(id);
    setEditName(productToEdit.name);
    setEditDescription(productToEdit.description);
    setSelectedBrand(productToEdit.brand.id);
    setSelectedCategory(productToEdit.category.id);
    setEditPrice(productToEdit.price);
    setEditedData({ ...productToEdit });
  };

  const handleSave = (id) => {
    const updatedProductData = {
      name: editName,
      description: editDescription,
      brand: { id: selectedBrand },
      category: { id: selectedCategory },
      price: editPrice,
    };
    console.log("PRODUCTO UPDATE  ", updatedProductData);

    fetch(`http://18.118.140.140/product/${id}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(updatedProductData),
    })
      .then((response) => {
        if (response.ok) {
          return response.text(); // Leer el mensaje de respuesta como texto
        } else {
          throw new Error(
            "Error updating product. HTTP status: " + response.status
          );
        }
      })
      .then((message) => {
        console.log("Mensaje del servidor:", message);

        // Get para traer los datos actulizados
        fetch(`http://18.118.140.140/product`)
          .then((response) => response.json())
          .then((data) => {
            setProductos(data);

            alert("Producto modificado con éxito");
          })
          .catch((error) => {
            console.error("Error fetching data:", error);
          });
      })
      .catch((error) => {
        console.error("Error updating the product:", error);
      });

    setEditingProduct(null);
  };

  const formatPrice = (price) => {
    return price.toLocaleString("en-US", {
      style: "currency",
      currency: "USD",
    });
  };

  return (
    <div className={styles.listaProductos}>
      <h2>Lista de Productos</h2>
      <table>
        <thead>
          <tr>
            <th>Id</th>
            <th>Imagen</th>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Marca</th>
            <th>Categoría</th>
            <th>Precio</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {productos.map((producto) => (
            <tr key={producto.id}>
              <td>{producto.id}</td>
              <td>
                <img
                  src={`http://18.118.140.140/s3/product-images/${producto.id}/0`}
                  alt={`Imagen de ${producto.name}`}
                />
              </td>
              <td>
                {editingProduct === producto.id ? (
                  <input
                    type="text"
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                  />
                ) : (
                  producto.name
                )}
              </td>
              <td>
                {editingProduct === producto.id ? (
                  <input
                    type="text"
                    value={editDescription}
                    onChange={(e) => setEditDescription(e.target.value)}
                  />
                ) : (
                  producto.description
                )}
              </td>
              <td>
                {editingProduct === producto.id ? (
                  <select
                    value={selectedBrand}
                    onChange={(e) => setSelectedBrand(e.target.value)}
                  >
                    {availableBrands.map((brandOption) => (
                      <option key={brandOption.id} value={brandOption.id}>
                        {brandOption.name}
                      </option>
                    ))}
                  </select>
                ) : (
                  producto.brand.name
                )}
              </td>

              <td>
                {editingProduct === producto.id ? (
                  <select
                    value={selectedCategory}
                    onChange={(e) => setSelectedCategory(e.target.value)}
                  >
                    {availableCategories.map((categoryOption) => (
                      <option key={categoryOption.id} value={categoryOption.id}>
                        {categoryOption.name}
                      </option>
                    ))}
                  </select>
                ) : (
                  producto.category.name
                )}
              </td>
              <td>
                {editingProduct === producto.id ? (
                  <input
                    type="text"
                    value={editPrice}
                    onChange={(e) => setEditPrice(e.target.value)}
                  />
                ) : (
                  formatPrice(producto.price)
                )}
              </td>
              <td>
                {editingProduct === producto.id ? (
                  <button onClick={() => handleSave(producto.id)}>
                    <FaSave />
                  </button>
                ) : (
                  <>
                    <button onClick={() => handleEdit(producto.id)}>
                      <FaEdit />
                    </button>
                    <button onClick={() => handleDelete(producto.id)}>
                      <FaTrash />
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ListaProductos;
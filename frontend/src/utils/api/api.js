import SERVER_URL from "../configurations/server";

// Función de utilidad para obtener detalles del producto e imágenes
export async function fetchProductAndImages(id) {
    try {
      // Hacer una solicitud para obtener tanto los detalles del producto como la lista de imágenes
      const [responseProduct, responseImages] = await Promise.all([
        fetch(`${SERVER_URL}/product/${id}`),
        fetch(`${SERVER_URL}/images/product/${id}`)
      ]);
  
      if (!responseProduct.ok) {
        throw new Error('Error al obtener los detalles del producto');
      }
  
      if (!responseImages.ok) {
        throw new Error('Error al obtener la lista de imágenes del producto');
      }
  
      const productJSON = await responseProduct.json();
      const imagesJSON = await responseImages.json();

      console.log("IMAGENES JSON: " + imagesJSON);
  
      // Construir las URL de las imágenes basadas en los nombres de los archivos
      const imagesArray = imagesJSON.map((imageInfo, index) => {
        return `${SERVER_URL}/images/product/${id}/${index}`;
      });
      console.log("IMAGENES ARRAY." + imagesArray)

      return { product: productJSON, images: imagesArray };

    } catch (error) {
      console.error('Error en la solicitud:', error);
      return { product: null, images: [] };
    }
  }

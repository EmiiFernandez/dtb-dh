import React, { createContext, useState, useEffect } from "react";
import axios from "axios";
import { toast } from "sonner";

export const DataContext = createContext();

const DataProvider = ({ children }) => {
 /*  const [data, setData] = useState([]); */
/*   const [cart, setCart] = useState([]); */
  const [user, setUser] = useState({});


/*   useEffect(() => {
    axios("./data.json")
      .then((res) => {
        setData(res.data);
      })
      .catch((error) => {
        console.error(error);
      });
  }, []); */
/* 
  const buyProducts = (e, product) => {
    // Agrega 'product' como parámetro

    const productRepeat = cart.find((item) => item.id === product.id);
    console.log(productRepeat);

    if (productRepeat) {
      setCart(cart.map((item)=>(item.id === product.id ? {...product, quanty:productRepeat.quanty + 1} : item)));
    } else {
      setCart([...cart, product])
    }

    e.preventDefault(); */

   /*  console.log("Comprando: ", product);
    toast.success(`${product.objeto} añadido al carrito!`);
  };

  console.log("data products: ", data); */


  return (
    <DataContext.Provider value={{ user, setUser}}>
      {children}
    </DataContext.Provider>
  );
  
};

export default DataProvider;

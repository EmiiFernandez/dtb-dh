import { Suspense } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Toaster } from "sonner";
import "./App.css";

import DataProvider from "./components/Context/DataContext";

import Navbar from "./components/Navbar/Navbar";
import Footer from "./components/Footer/Footer";
import navigation from "./Routes/Routes"; // Importa la configuración de rutas

function App() {
  return (
    <Suspense fallback={<h1>Hola, Cargando tu página...</h1>}>
      <DataProvider>
        <BrowserRouter>
        <div className="app-container">
            <Routes>
              {navigation.map(({ id, path, Element }) => (
                <Route
                  key={id}
                  path={path}
                  element={
                    <>
                      <Navbar />
                      <div className="main-content">
                        <Element />
                      </div>
                      <Footer />
                    </>
                  }
                />
              ))}
            </Routes>
          </div>
        </BrowserRouter>
        <Toaster position="top-center" expand={true} richColors />
      </DataProvider>
    </Suspense>
  );
}

export default App;


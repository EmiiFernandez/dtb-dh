import React, { useContext } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { AuthContext } from "../components/Context/AuthContext";

const ProtectedRoutes = () => {
  const {isLogged} = useContext(AuthContext);

  if (isLogged) {
    return <Outlet />;
  }
  return <Navigate to="/login" />;

};

export default ProtectedRoutes;
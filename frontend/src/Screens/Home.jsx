import React from 'react';
import Recomendados from '../components/Home/Recomendados/Recomendados';
import DescripcionProductosManejados from '../components/Home/DescripcionProductosManejados/DescripcionProductosManejados';
import Confian from '../components/Home/Confian/Confian';
import Contactanos from '../components/Home/Contactanos/Contactanos';
import Buscador from '../components/Home/Buscador/Buscador';


const Home = () => {
  return (
    <div> 
        <Buscador />
        <Recomendados/>
        <DescripcionProductosManejados/>
        <Confian/>
        <Contactanos/>
    </div>
  );
};

export default Home;

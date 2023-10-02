import styles from '../Contactanos/contactanos.module.css'



function Contactanos() {
    return(
        <>
            <div className={styles.contenedorPadre}>
                <section className={styles.textoContacto}>
                    <span>¿Tenés Dudas?</span>
                    <span>Comunicate con nosotros</span>
                </section>

                <a href="/Contacto" className={styles.contactoButton}>
                 Contacto
                </a>
            </div>
        </>
    )
}

export default Contactanos;
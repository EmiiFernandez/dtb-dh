import styles from '../DescripcionProductosManejados/productosManejados.module.css'

function DescripcionProductosManejados() {
    return(
        <>
        <div className={styles.contenedorPadre}>
            {/* Por cada fila (texto + imagen + separador) */}
            <span className={styles.title}>Qué productos manejamos</span>

            {/* FILA 1 */}
            <section className={styles.fila}>
                <section className={styles.content}>
                    <div className={styles.descripcion}>
                        <span className={styles.tituloDescripcion}>Bandejas giradiscos</span>
                        <p className={styles.paragraph}>
                            Desde AudioTechnica hasta Technics,
                            si tu música gira alrededor de un vinilo, 
                            tus bandejas las encontrás acá
                        </p>
                    </div>
                    <img src="https://http2.mlstatic.com/D_NQ_NP_689720-MLA69887987939_062023-O.webp" alt="BandejaGiradiscos" width={"50%"}/>
                </section>
                <hr className={styles.divider}/>
            </section>


            {/* FILA 2 */}
            <section className={styles.fila}>
                {/* Imagen y texto */}
                <section className={styles.content}>
                    {/* Texto */}
                    <img src="https://http2.mlstatic.com/D_NQ_NP_760831-MLA46856420992_072021-O.webp" alt="tecladoMIDI" width={"50%"}/>
                    <div className={styles.descripcion}>
                        <span className={styles.tituloDescripcion}>Teclados MIDI</span>
                        <p className={styles.paragraph}>
                            ¿Estás arrancando a producir?, estos teclados son compatibles con los DAW mas conocidos en el mercado, sentite un tecladista profesional. Sampleá como loco, estos son tu mejor amigo
                        </p>
                    </div>
                    
                </section>
                <hr className={styles.divider}/>
            </section>


            {/* FILA 3 */}
            <section className={styles.fila}>
                {/* Imagen y texto */}
                <section className={styles.content}>
                    {/* Texto */}
                    <div className={styles.descripcion}>
                        <span className={styles.tituloDescripcion}>Auriculares</span>
                        <p className={styles.paragraph}>
                            Nada es para cualquiera, llevate los mejores auriculares, tanto para uso personal como para producción musical
                        </p>
                    </div>
                    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSuhAZYPUWEIQkNKFoXbBm2_09gSU-qIRqqTA&usqp=CAU" alt="auriculares" width={"50%"}/>
                    
                </section>
                <hr className={styles.divider}/>
            </section>
        </div>
        </>
    )
}

export default DescripcionProductosManejados;
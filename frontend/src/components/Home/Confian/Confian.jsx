import styles from '../Confian/confian.module.css'

function Confian() {
    return(
        <>
        <div className={styles.contenedorPadre}>
            <span className={styles.title}>Confían en nosotros</span>

            {/* MARCAS */}
            <section className={styles.marcasContainer}>
                <a href="http://mx.yamaha.com/index.html" target="_blank" rel="noopener noreferrer">
                    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQDkbdRnUz15OwDV1VEz5wXqS0K151v2vXDbA&usqp=CAU" alt="YAMAHA" />
                </a>
                <a href="http://www.behringer.com/" target="_blank" rel="noopener noreferrer">
                    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSGtkG5SeEVFP1MZRy2ZA139AXBbUvQpdcrEDe3Qsh8KFflRz_EWwgPUPCdwuOJR1-uJpo&usqp=CAU" alt="BEHRINGER" />
                </a>
                <a href="https://www.babilonguitars.com/" target="_blank" rel="noopener noreferrer">
                    <img src="https://cdn.shopify.com/s/files/1/1610/5893/collections/Babilon-logo-dorado.jpg?v=1659212836" alt="BABILON" />
                </a>
                <a href="http://www.casio.com/latin/electronic-musical-instruments/" target="_blank" rel="noopener noreferrer">
                    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTeTyFUpewYiS1gsZ-FOyAr43LLUvh70Wcy54t9fkoeVJ25DVRU1zj1dhtxmUx3OGLA2ds&usqp=CAU" alt="CASIO" />
                </a>
                <a href="https://www.shure.com/es-LATAM" target="_blank" rel="noopener noreferrer">
                    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTcxNydq61zkIDROBo-C4-VNAlq_tNxoZgCPg&usqp=CAU" alt="SHURE" />
                </a>
                <a href="https://www.pioneerdj.com/en/" target="_blank" rel="noopener noreferrer">
                    <img src="https://d2r9epyceweg5n.cloudfront.net/stores/001/258/508/products/pioneer-dj-logo1-3c06ccc372433ca3d016885836781562-480-0.webp" alt="PIONEER DJ" />
                </a>
                <a href="https://www.sennheiserstore.com.ar/" target="_blank" rel="noopener noreferrer">
                    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSoInJ5bIfBPkUvC9YsRWL4Y-RRC84o33vuT_SPgopO-pyts83zXijtinLieEz8nSEUCk8&usqp=CAU" alt="SENNHEISER" />
                </a>
            </section>
            
            <hr className={styles.divider}/>
        </div>
        </>
    )
}

export default Confian;
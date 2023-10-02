import React from 'react'
import styles from "./footer.module.css"
import logo from '../../DropTheBass.png'
import { BsFacebook, BsInstagram, BsLinkedin, BsTwitter} from "react-icons/bs";

const Footer = () => {
return (

    <div className={styles.footerContainer} >
        <article className={styles.footerBox}>
          <img className={styles.footerLogo} src={logo} alt="logoDropBass" />
          <div className={styles.footerLinks}>

                <a href="https://www.facebook.com"><BsFacebook color='black'/></a>
                <a href="https://www.twitter.com"><BsTwitter color='black'/></a>
                <a href="https://www.linkedin.com"><BsLinkedin color='black'/></a>
                <a href="https://www.instagram.com"><BsInstagram color='black'/></a> 
          
          </div>
        </article>
        <p className={styles.date}>&copy; {new Date().getFullYear()} Copyright. All rights reserved.</p>
    </div>

)
}

export default Footer
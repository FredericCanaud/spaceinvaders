package fr.unilim.iut.spaceinvaders.model;

import java.util.ArrayList;
import java.util.List;

import fr.unilim.iut.spaceinvaders.moteurjeu.Commande;
import fr.unilim.iut.spaceinvaders.moteurjeu.Jeu;
import fr.unilim.iut.spaceinvaders.utils.DebordementEspaceJeuException;
import fr.unilim.iut.spaceinvaders.utils.HorsEspaceJeuException;
import fr.unilim.iut.spaceinvaders.utils.MissileException;

public class SpaceInvaders implements Jeu {

	int longueur;
	int hauteur;
	Vaisseau vaisseau;
	List<Missile> missiles;
	private long timerMissile;
	Envahisseur envahisseur;
	boolean partieFinie;

	public SpaceInvaders(int longueur, int hauteur) {
		this.longueur = longueur;
		this.hauteur = hauteur;
		this.missiles = new ArrayList<Missile>();
	}

	public void initialiserJeu() {
		this.partieFinie = false;
		Position positionVaisseau = new Position(Constante.ESPACEJEU_LONGUEUR / 2, Constante.ESPACEJEU_HAUTEUR - 1);
		Dimension dimensionVaisseau = new Dimension(Constante.VAISSEAU_LONGUEUR, Constante.VAISSEAU_HAUTEUR);
		Position positionEnvahisseur = new Position(Constante.ESPACEJEU_LONGUEUR / 2,
				Constante.ENVAHISSEUR_POSITION_VERTICALE);
		Dimension dimensionEnvahisseur = new Dimension(Constante.ENVAHISSEUR_LONGUEUR, Constante.ENVAHISSEUR_HAUTEUR);
		positionnerUnNouveauVaisseau(dimensionVaisseau, positionVaisseau, Constante.VAISSEAU_VITESSE);
		positionnerUnNouveauEnvahisseur(dimensionEnvahisseur, positionEnvahisseur, Constante.ENVAHISSEUR_VITESSE);
	}

	public String recupererEspaceJeuDansChaineASCII() {
		StringBuilder espaceDeJeu = new StringBuilder();

		for (int y = 0; y < hauteur; y++) {
			for (int x = 0; x < longueur; x++) {
				espaceDeJeu.append(recupererMarqueDeLaPosition(x, y));
			}
			espaceDeJeu.append(Constante.MARQUE_FIN_LIGNE);
		}
		return espaceDeJeu.toString();
	}

	private char recupererMarqueDeLaPosition(int x, int y) {
		char marque;

		if (this.aUnVaisseauQuiOccupeLaPosition(x, y)) {
			marque = Constante.MARQUE_VAISSEAU;
		} else if (this.aUnMissileQuiOccupeLaPosition(x, y)) {
			marque = Constante.MARQUE_MISSILE;
		} else if (this.aUnEnvahisseurQuiOccupeLaPosition(x, y)) {
			marque = Constante.MARQUE_ENVAHISSEUR;
		} else {
			marque = Constante.MARQUE_VIDE;
		}
		return marque;
	}

	private boolean aUnMissileQuiOccupeLaPosition(int x, int y) {
		if ( this.aDesMissiles() ) {
            for (Missile missile : this.missiles) {
                if (missile.occupeLaPosition(x, y))
                    return true;
            }
        }
        return false;
	}

	private boolean aUnVaisseauQuiOccupeLaPosition(int x, int y) {
		return this.aUnVaisseau() && vaisseau.occupeLaPosition(x, y);
	}

	private boolean aUnEnvahisseurQuiOccupeLaPosition(int x, int y) {
		return this.aUnEnvahisseur() && envahisseur.occupeLaPosition(x, y);
	}

	public boolean aDesMissiles() {
		return !this.missiles.isEmpty();
	}

	public boolean aUnVaisseau() {
		return vaisseau != null;
	}

	public boolean aUnEnvahisseur() {
		return envahisseur != null;
	}

	public void positionnerUnNouveauVaisseau(Dimension dimension, Position position, int vitesse) {

		int x = position.abscisse();
		int y = position.ordonnee();

		if (!estDansEspaceJeu(x, y)) {
			throw new HorsEspaceJeuException("La position du vaisseau est en dehors de l'espace jeu");
		}

		int longueurVaisseau = dimension.longueur();
		int hauteurVaisseau = dimension.hauteur();

		if (!estDansEspaceJeu(x + longueurVaisseau - 1, y)) {
			throw new DebordementEspaceJeuException(
					"Le vaisseau déborde de l'espace jeu vers la droite à cause de sa longueur");
		}
		if (!estDansEspaceJeu(x, y - hauteurVaisseau + 1)) {
			throw new DebordementEspaceJeuException(
					"Le vaisseau déborde de l'espace jeu vers le bas à cause de sa hauteur");
		}

		vaisseau = new Vaisseau(dimension, position, vitesse);
	}

	public void positionnerUnNouveauEnvahisseur(Dimension dimension, Position position, int vitesse) {
		int x = position.abscisse();
		int y = position.ordonnee();

		if (!estDansEspaceJeu(x, y))
			throw new HorsEspaceJeuException("La position de l'envahisseur est en dehors de l'espace jeu");

		int longueurVaisseau = dimension.longueur();
		int hauteurVaisseau = dimension.hauteur();

		if (!estDansEspaceJeu(x + longueurVaisseau - 1, y))
			throw new DebordementEspaceJeuException(
					"Le vaisseau déborde de l'espace jeu vers la droite à cause de sa longueur");
		if (!estDansEspaceJeu(x, y - hauteurVaisseau + 1))
			throw new DebordementEspaceJeuException(
					"Le vaisseau déborde de l'espace jeu vers le bas à cause de sa hauteur");

		envahisseur = new Envahisseur(dimension, position, vitesse);
	}

	private boolean estDansEspaceJeu(int x, int y) {
		return (((x >= 0) && (x < longueur)) && ((y >= 0) && (y < hauteur)));
	}

	public void deplacerVaisseauVersLaDroite() {
		if (vaisseau.abscisseLaPlusADroite() < (longueur - 1)) {
			vaisseau.deplacerHorizontalementVers(Direction.DROITE);
			if (!estDansEspaceJeu(vaisseau.abscisseLaPlusADroite(), vaisseau.ordonneeLaPlusHaute())) {
				vaisseau.positionner(longueur - vaisseau.longueur(), vaisseau.ordonneeLaPlusHaute());
			}
		}
	}

	public void deplacerVaisseauVersLaGauche() {
		if (0 < vaisseau.abscisseLaPlusAGauche())
			vaisseau.deplacerHorizontalementVers(Direction.GAUCHE);
		if (!estDansEspaceJeu(vaisseau.abscisseLaPlusAGauche(), vaisseau.ordonneeLaPlusHaute())) {
			vaisseau.positionner(0, vaisseau.ordonneeLaPlusHaute());
		}
	}

	public void deplacerMissile(Direction direction) {
		for (Missile missile : this.missiles) {
            missile.deplacerVerticalementVers(direction);
        }
		supprrimerMissilesHorsEspaceDeJeu();
	}

	public void deplacerEnvahisseur() {
		if (Direction.DROITE.equals(envahisseur.getSensDeplacement())
				&& envahisseur.abscisseLaPlusADroite() >= longueur - 1)
			envahisseur.setSensDeplacement(Direction.GAUCHE);
		if (Direction.GAUCHE.equals(envahisseur.getSensDeplacement()) && envahisseur.abscisseLaPlusAGauche() <= 0)
			envahisseur.setSensDeplacement(Direction.DROITE);
		envahisseur.deplacerHorizontalementVers(envahisseur.getSensDeplacement());
	}
	private void supprrimerMissilesHorsEspaceDeJeu() {
        boolean continuation;
        do {
            continuation = false;
            for (Missile missile : this.missiles) {
                if (missile.ordonneeLaPlusHaute() <= 0) {
                    this.missiles.remove(missile);
                    continuation = true;
                    break;
                }
            }
        } while (continuation);
    }
	public void evoluer(Commande commande) {
		if (null != commande) {
			if (commande.gauche) {
				this.deplacerVaisseauVersLaGauche();
			}
			if (commande.droite) {
				this.deplacerVaisseauVersLaDroite();
			}
			if (commande.tir) {
				this.tirerUnMissileDepuisVaisseau(new Dimension(Constante.MISSILE_LONGUEUR, Constante.MISSILE_HAUTEUR),
						Constante.MISSILE_VITESSE);
			}
		}
		if (this.aDesMissiles())
			this.deplacerMissile(Direction.HAUT_ECRAN);
		if (this.aUnEnvahisseur()) {
			this.deplacerEnvahisseur();
		}
		for (Missile missile : this.missiles) {
            if (this.aUnEnvahisseur() && this.aDesMissiles() && (new Collision()).detecterCollision(envahisseur, missile)) {
            	this.envahisseur = null;
    			missile = null;
    			this.partieFinie = true;
            }
		}
	}

	public boolean etreFini() {
		return this.partieFinie;
	}

	public Vaisseau recupererVaisseau() {
		return this.vaisseau;
	}

	public List<Missile> recupererMissiles() {
        return this.missiles;
    }

	public Envahisseur recupererEnvahisseur() {
		return this.envahisseur;
	}

	public void tirerUnMissileDepuisVaisseau(Dimension dimensionMissile, int vitesseMissile) {

		if ((vaisseau.hauteur() + dimensionMissile.hauteur()) > this.hauteur)
			throw new MissileException(
					"Pas assez de hauteur libre entre le vaisseau et le haut de l'espace jeu pour tirer le missile");

		if (System.currentTimeMillis() > this.timerMissile + Constante.TEMPS_ENTRE_DEUX_MISSILES) {
			missiles.add(this.vaisseau.tirerUnMissile(dimensionMissile, vitesseMissile, Direction.HAUT_ECRAN));
			this.timerMissile = System.currentTimeMillis();
		}	}

}
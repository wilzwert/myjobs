package com.wilzwert.myjobs.core.domain.service.metadata;


import com.wilzwert.myjobs.core.domain.shared.exception.MalformedUrlException;
import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.JobMetadataExtractorService;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.HtmlFetcherService;
import com.wilzwert.myjobs.core.domain.model.job.service.JobMetadataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.TestFileLoader;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 */

@ExtendWith(MockitoExtension.class)
class JobMetadataServiceTest {
    @Mock
    private HtmlFetcherService fetcherService;

    @Mock
    private JobMetadataExtractorService jobMetadataExtractorService;

    private JobMetadataService jobMetadataService;

    @BeforeEach
    void setUp() {
        jobMetadataService = new JobMetadataService(fetcherService, jobMetadataExtractorService);
    }

    @Test
    void testGetDomainFromInvalidUrl() {
        assertThrows(MalformedUrlException.class, () -> jobMetadataService.getUrlDomain("not_an_url"));
    }

    @Test
    void testGetDomainFromIncompatibleUrl() {
        assertThrows(MalformedUrlException.class, () -> jobMetadataService.getUrlDomain("ftp://my.ftp.example.com"));
    }

    @Test
    void testGetUrlDomainWithoutSubdomain() {
        assertEquals("example.com", jobMetadataService.getUrlDomain("http://example.com/uri"));
    }

    @Test
    void testGetUrlDomainWithOneSubdomain() {
        assertEquals("www.example.com", jobMetadataService.getUrlDomain("https://www.example.com/uri"));
    }

    @Test
    void testGetUrlDomainWithMultipleSubdomain() {
        assertEquals("w3.www.fr.example.com", jobMetadataService.getUrlDomain("https://w3.www.fr.example.com/uri"));
    }

    @Test
    void testGetUrlDomainWithSubdomain() {
        assertEquals("emploi.fhf.fr", jobMetadataService.getUrlDomain("https://emploi.fhf.fr/emploi/414440"));
    }

    @Test
    void testEmptyHtmlExtraction() {
        when(fetcherService.fetchHtml(anyString(), anyString())).thenReturn(Optional.empty());

        JobMetadata expectedJobMetadata = new JobMetadata.Builder()
                .url("http://example.com")
                .build();

        assertEquals(expectedJobMetadata, jobMetadataService.extractMetadata("http://example.com"));
    }

    @Test
    void testCompatibleHtmlExtraction() throws IOException {
        when(fetcherService.fetchHtml(anyString(), anyString())).thenReturn(Optional.ofNullable(TestFileLoader.loadFileAsString("jobposting.jsonld.salary.monetaryamount.html")));
        when(jobMetadataExtractorService.extractJobMetadata(anyString(), anyString())).thenReturn(Optional.of(new JobMetadata.Builder()
                .title("Formateur equipes commerciales h/f")
                .description("<p>Quelles sont les missions ?</p><p>&nbsp;Rejoignez Le Service Formation !<br/>Votre r&ocirc;le&nbsp;<br/>Dans le cadre de notre d&eacute;veloppement, nous recherchons&nbsp;un Formateur Interne au niveau national&nbsp;d&eacute;di&eacute; Commerce,&nbsp;pour renforcer notre &eacute;quipe de formateurs. V&eacute;ritable ambassadeur de la performance commerciale et de l'excellence, vous jouerez un r&ocirc;le cl&eacute; dans le d&eacute;veloppement des comp&eacute;tences de nos &eacute;quipes commerciales.<br/>Vous accompagnerez nos collaborateurs commerciaux vers le succ&egrave;s en assurant leur formation + pilotage Sales Academy.<br/>Formation aux techniques de vente :<br/>\t-\tConcevoir, animer et &eacute;valuer des formations&nbsp;aux techniques de vente et sur nos produits et services<br/>\t-\tAssurer un suivi r&eacute;gulier des performances des collaborateurs form&eacute;s<br/>\t-\tAccompagnement terrain des commerciaux<br/>\t-\tIdentifier les besoins de formation et optimiser les processus d'apprentissage en collaboration avec avec la direction commerciale<br/></p><p>Quel est le profil idéal ?</p><p>Ing&eacute;nierie p&eacute;dagogique&nbsp;:<br/>\t-\tAdapter les contenus p&eacute;dagogiques en fonction des besoins des &eacute;quipes et des &eacute;volutions du march&eacute;.<br/>Veille et innovation :<br/>\t-\tProposer des outils et formats innovants pour favoriser l'apprentissage (e-learning, ateliers pratiques, &eacute;tudes de cas, etc.).<br/>\t-\tAssurer une veille sur les meilleures pratiques commerciales et les nouvelles m&eacute;thodes de formation<br/>Intervenant dans la Sales Academy et int&eacute;gration des nouveaux commerciaux :<br/>\t-\tStructurer et coordonner la Sales Academy, garantissant un parcours de formation coh&eacute;rent et efficace pour les nouveaux arrivants<br/>\t-\tOrganiser et superviser l'int&eacute;gration des nouveaux commerciaux pour assurer une mont&eacute;e en comp&eacute;tence rapide<br/>Ce poste est fait pour vous si ....<br/>Vous justifiez d'une exp&eacute;rience r&eacute;ussie dans le commerce dans la vente ou le management commercial et d'une exp&eacute;rience en formation<br/>\t-\tComp&eacute;tences p&eacute;dagogiques : capacit&eacute; &agrave; transmettre vos connaissances et &agrave; engager<br/>\t-\tOrganisation et leadership : piloter des projets, structurer des parcours de formation et mobiliser des &eacute;quipes.<br/>\t-\tD&eacute;placements &agrave; pr&eacute;voir dans toute la France 3j/semaine<br/>Pourquoi nous rejoindre ?<br/>\t-\tUn groupe International en croissance depuis plus de 10 ans<br/>\t-\tUn parcours d'int&eacute;gration et formation aux produits et services<br/>\t-\tUn management de proximit&eacute;, des &eacute;quipes dynamiques<br/>\t-\tOpportunit&eacute;s de d&eacute;veloppement professionnel et carri&egrave;re<br/>Avantages<br/>\t-\tMutuelle / Pr&eacute;voyance / CE / Compte Epargne Temps / RTT&nbsp;<br/>\t-\tVoiture de fonction&nbsp;<br/>R&eacute;mun&eacute;ration&nbsp;:&nbsp; &agrave; partir de 50K&curren; +10% de variable&nbsp;<br/>Statut cadre<br/>Visitez notre site :&nbsp;&nbsp;&nbsp;https://careers.rentokil-initial.com/fr/<br/>Rentosj<br/></p><p>Qui a publié cette offre ?</p><p>D&eacute;couvrez un univers professionnel &eacute;tonnant !<br/><br/>Depuis 1923, Initial Textile, acteur majeur de la blanchisserie industrielle, accompagne les soci&eacute;t&eacute;s qui souhaitent externaliser le nettoyage de leurs v&ecirc;tements et linge professionnels. <br/><br/>Plus de 50 000 clients de l'industrie, de l'h&ocirc;tellerie-restauration, artisans et commerces de proximit&eacute; nous font confiance.<br/><br/>Nos 25 sites en France g&egrave;rent la location/entretien du linge et la livraison avec l'ambition d'apporter le meilleur niveau de service.<br/><br/>Seul expert de ce march&eacute; &agrave; r&eacute;aliser la cr&eacute;ation de v&ecirc;tements, nos gammes allient confort, style et s&eacute;curit&eacute;, adapt&eacute;es aux sp&eacute;cificit&eacute;s de chaque m&eacute;tier. La tra&ccedil;abilit&eacute; de nos articles, apporte une valeur ajout&eacute;e unique &agrave; notre prestation de service.<br/><br/>Dans le cadre de notre politique RSE, 100% du linge est recycl&eacute; vers d'autres industries et nous poursuivons nos engagements sur la transition &eacute;nerg&eacute;tique.<br/><br/><br/><br/></p>")
                .profile("Ingénierie pédagogique :\n\t-\tAdapter les contenus pédagogiques en fonction des besoins des équipes et des évolutions du marché.\nVeille et innovation :\n\t-\tProposer des outils et formats innovants pour favoriser l'apprentissage (e-learning, ateliers pratiques, études de cas, etc.).\n\t-\tAssurer une veille sur les meilleures pratiques commerciales et les nouvelles méthodes de formation\nIntervenant dans la Sales Academy et intégration des nouveaux commerciaux :\n\t-\tStructurer et coordonner la Sales Academy, garantissant un parcours de formation cohérent et efficace pour les nouveaux arrivants\n\t-\tOrganiser et superviser l'intégration des nouveaux commerciaux pour assurer une montée en compétence rapide\nCe poste est fait pour vous si ....\nVous justifiez d'une expérience réussie dans le commerce dans la vente ou le management commercial et d'une expérience en formation\n\t-\tCompétences pédagogiques : capacité à transmettre vos connaissances et à engager\n\t-\tOrganisation et leadership : piloter des projets, structurer des parcours de formation et mobiliser des équipes.\n\t-\tDéplacements à prévoir dans toute la France 3j/semaine\nPourquoi nous rejoindre ?\n\t-\tUn groupe International en croissance depuis plus de 10 ans\n\t-\tUn parcours d'intégration et formation aux produits et services\n\t-\tUn management de proximité, des équipes dynamiques\n\t-\tOpportunités de développement professionnel et carrière\nAvantages\n\t-\tMutuelle / Prévoyance / CE / Compte Epargne Temps / RTT \n\t-\tVoiture de fonction \nRémunération :  à partir de 50K? +10% de variable \nStatut cadre\nVisitez notre site :   https://careers.rentokil-initial.com/fr/\nRentosj\n")
                .url(null)
                .company("RENTOKIL INITIAL")
                .salary("30000 - 60000 EUR / YEAR")
                .build()))
        ;

        JobMetadata expectedJobMetadata = new JobMetadata.Builder()
                .title("Formateur equipes commerciales h/f")
                .description("<p>Quelles sont les missions ?</p><p>&nbsp;Rejoignez Le Service Formation !<br/>Votre r&ocirc;le&nbsp;<br/>Dans le cadre de notre d&eacute;veloppement, nous recherchons&nbsp;un Formateur Interne au niveau national&nbsp;d&eacute;di&eacute; Commerce,&nbsp;pour renforcer notre &eacute;quipe de formateurs. V&eacute;ritable ambassadeur de la performance commerciale et de l'excellence, vous jouerez un r&ocirc;le cl&eacute; dans le d&eacute;veloppement des comp&eacute;tences de nos &eacute;quipes commerciales.<br/>Vous accompagnerez nos collaborateurs commerciaux vers le succ&egrave;s en assurant leur formation + pilotage Sales Academy.<br/>Formation aux techniques de vente :<br/>\t-\tConcevoir, animer et &eacute;valuer des formations&nbsp;aux techniques de vente et sur nos produits et services<br/>\t-\tAssurer un suivi r&eacute;gulier des performances des collaborateurs form&eacute;s<br/>\t-\tAccompagnement terrain des commerciaux<br/>\t-\tIdentifier les besoins de formation et optimiser les processus d'apprentissage en collaboration avec avec la direction commerciale<br/></p><p>Quel est le profil idéal ?</p><p>Ing&eacute;nierie p&eacute;dagogique&nbsp;:<br/>\t-\tAdapter les contenus p&eacute;dagogiques en fonction des besoins des &eacute;quipes et des &eacute;volutions du march&eacute;.<br/>Veille et innovation :<br/>\t-\tProposer des outils et formats innovants pour favoriser l'apprentissage (e-learning, ateliers pratiques, &eacute;tudes de cas, etc.).<br/>\t-\tAssurer une veille sur les meilleures pratiques commerciales et les nouvelles m&eacute;thodes de formation<br/>Intervenant dans la Sales Academy et int&eacute;gration des nouveaux commerciaux :<br/>\t-\tStructurer et coordonner la Sales Academy, garantissant un parcours de formation coh&eacute;rent et efficace pour les nouveaux arrivants<br/>\t-\tOrganiser et superviser l'int&eacute;gration des nouveaux commerciaux pour assurer une mont&eacute;e en comp&eacute;tence rapide<br/>Ce poste est fait pour vous si ....<br/>Vous justifiez d'une exp&eacute;rience r&eacute;ussie dans le commerce dans la vente ou le management commercial et d'une exp&eacute;rience en formation<br/>\t-\tComp&eacute;tences p&eacute;dagogiques : capacit&eacute; &agrave; transmettre vos connaissances et &agrave; engager<br/>\t-\tOrganisation et leadership : piloter des projets, structurer des parcours de formation et mobiliser des &eacute;quipes.<br/>\t-\tD&eacute;placements &agrave; pr&eacute;voir dans toute la France 3j/semaine<br/>Pourquoi nous rejoindre ?<br/>\t-\tUn groupe International en croissance depuis plus de 10 ans<br/>\t-\tUn parcours d'int&eacute;gration et formation aux produits et services<br/>\t-\tUn management de proximit&eacute;, des &eacute;quipes dynamiques<br/>\t-\tOpportunit&eacute;s de d&eacute;veloppement professionnel et carri&egrave;re<br/>Avantages<br/>\t-\tMutuelle / Pr&eacute;voyance / CE / Compte Epargne Temps / RTT&nbsp;<br/>\t-\tVoiture de fonction&nbsp;<br/>R&eacute;mun&eacute;ration&nbsp;:&nbsp; &agrave; partir de 50K&curren; +10% de variable&nbsp;<br/>Statut cadre<br/>Visitez notre site :&nbsp;&nbsp;&nbsp;https://careers.rentokil-initial.com/fr/<br/>Rentosj<br/></p><p>Qui a publié cette offre ?</p><p>D&eacute;couvrez un univers professionnel &eacute;tonnant !<br/><br/>Depuis 1923, Initial Textile, acteur majeur de la blanchisserie industrielle, accompagne les soci&eacute;t&eacute;s qui souhaitent externaliser le nettoyage de leurs v&ecirc;tements et linge professionnels. <br/><br/>Plus de 50 000 clients de l'industrie, de l'h&ocirc;tellerie-restauration, artisans et commerces de proximit&eacute; nous font confiance.<br/><br/>Nos 25 sites en France g&egrave;rent la location/entretien du linge et la livraison avec l'ambition d'apporter le meilleur niveau de service.<br/><br/>Seul expert de ce march&eacute; &agrave; r&eacute;aliser la cr&eacute;ation de v&ecirc;tements, nos gammes allient confort, style et s&eacute;curit&eacute;, adapt&eacute;es aux sp&eacute;cificit&eacute;s de chaque m&eacute;tier. La tra&ccedil;abilit&eacute; de nos articles, apporte une valeur ajout&eacute;e unique &agrave; notre prestation de service.<br/><br/>Dans le cadre de notre politique RSE, 100% du linge est recycl&eacute; vers d'autres industries et nous poursuivons nos engagements sur la transition &eacute;nerg&eacute;tique.<br/><br/><br/><br/></p>")
                .profile("Ingénierie pédagogique :\n\t-\tAdapter les contenus pédagogiques en fonction des besoins des équipes et des évolutions du marché.\nVeille et innovation :\n\t-\tProposer des outils et formats innovants pour favoriser l'apprentissage (e-learning, ateliers pratiques, études de cas, etc.).\n\t-\tAssurer une veille sur les meilleures pratiques commerciales et les nouvelles méthodes de formation\nIntervenant dans la Sales Academy et intégration des nouveaux commerciaux :\n\t-\tStructurer et coordonner la Sales Academy, garantissant un parcours de formation cohérent et efficace pour les nouveaux arrivants\n\t-\tOrganiser et superviser l'intégration des nouveaux commerciaux pour assurer une montée en compétence rapide\nCe poste est fait pour vous si ....\nVous justifiez d'une expérience réussie dans le commerce dans la vente ou le management commercial et d'une expérience en formation\n\t-\tCompétences pédagogiques : capacité à transmettre vos connaissances et à engager\n\t-\tOrganisation et leadership : piloter des projets, structurer des parcours de formation et mobiliser des équipes.\n\t-\tDéplacements à prévoir dans toute la France 3j/semaine\nPourquoi nous rejoindre ?\n\t-\tUn groupe International en croissance depuis plus de 10 ans\n\t-\tUn parcours d'intégration et formation aux produits et services\n\t-\tUn management de proximité, des équipes dynamiques\n\t-\tOpportunités de développement professionnel et carrière\nAvantages\n\t-\tMutuelle / Prévoyance / CE / Compte Epargne Temps / RTT \n\t-\tVoiture de fonction \nRémunération :  à partir de 50K? +10% de variable \nStatut cadre\nVisitez notre site :   https://careers.rentokil-initial.com/fr/\nRentosj\n")
                .url("http://example.com")
                .company("RENTOKIL INITIAL")
                .salary("30000 - 60000 EUR / YEAR")
                .build();

        assertEquals(expectedJobMetadata, jobMetadataService.extractMetadata("http://example.com"));
    }
}

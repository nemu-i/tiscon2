package net.unit8.sigcolle.controller;

import javax.inject.Inject;
import javax.transaction.Transactional;

import enkan.component.doma2.DomaProvider;
import enkan.data.Flash;
import enkan.data.HttpResponse;
import kotowari.component.TemplateEngine;
import net.unit8.sigcolle.dao.CampaignDao;
import net.unit8.sigcolle.dao.SignatureDao;
import net.unit8.sigcolle.form.CampaignForm;
import net.unit8.sigcolle.form.LoginForm;
import net.unit8.sigcolle.form.SignatureForm;
import net.unit8.sigcolle.model.UserCampaign;
import net.unit8.sigcolle.model.Signature;
import net.unit8.sigcolle.model.Campaign;

import static enkan.util.BeanBuilder.builder;
import static enkan.util.HttpResponseUtils.RedirectStatusCode.SEE_OTHER;
import static enkan.util.HttpResponseUtils.redirect;
import static enkan.util.ThreadingUtils.some;

/**
 * @author kawasima
 */
public class CampaignController {
    @Inject
    private TemplateEngine templateEngine;

    @Inject
    private DomaProvider domaProvider;

    private HttpResponse showCampaign(Long campaignId, SignatureForm signature, String message) {
        CampaignDao campaignDao = domaProvider.getDao(CampaignDao.class);
        UserCampaign campaign = campaignDao.selectById(campaignId);

        SignatureDao signatureDao = domaProvider.getDao(SignatureDao.class);
        int signatureCount = signatureDao.countByCampaignId(campaignId);

        return templateEngine.render("campaign",
                "campaign", campaign,
                "signatureCount", signatureCount,
                "signature", signature,
                "message", message
        );
    }

    /**
     * キャンペーン詳細画面表示.
     * @param form URLパラメータ
     * @param flash flash scope session
     * @return HttpResponse
     */
    public HttpResponse index(CampaignForm form, Flash flash) {
        if (form.hasErrors()) {
            return builder(HttpResponse.of("Invalid"))
                    .set(HttpResponse::setStatus, 400)
                    .build();
        }

        return showCampaign(form.getCampaignIdLong(),
                new SignatureForm(),
                (String) some(flash, Flash::getValue).orElse(null));
    }

    /**
     * 署名の追加処理.
     * @param form 画面入力された署名情報.
     * @return HttpResponse
     */
    @Transactional
    public HttpResponse sign(SignatureForm form) {
        if (form.hasErrors()) {
            return showCampaign(form.getCampaignIdLong(), form, null);
        }
        Signature signature = builder(new Signature())
                .set(Signature::setCampaignId, form.getCampaignIdLong())
                .set(Signature::setName, form.getName())
                .set(Signature::setSignatureComment, form.getSignatureComment())
                .build();
        SignatureDao signatureDao = domaProvider.getDao(SignatureDao.class);
        signatureDao.insert(signature);

        return builder(redirect("/campaign/" + form.getCampaignId(), SEE_OTHER))
                .set(HttpResponse::setFlash, new Flash("ご賛同ありがとうございました！"))
                .build();
    }

    /**
     * 新規キャンペーン作成画面表示.
     * @return HttpResponse
     */
    public HttpResponse createForm() {
        return templateEngine.render("signature/new", "campaign", new CampaignForm());
    }

    /**
     * 新規キャンペーン作成処理.
     * @return HttpResponse
     */
    public HttpResponse create(CampaignForm form) {
        // TODO: create campaign

        if (form.hasErrors()) {
            return templateEngine.render("signature/new", "campaign", form);
        }

        CampaignDao cDao = domaProvider.getDao(CampaignDao.class);

        Campaign cam = builder(new Campaign())
                //.set(Campaign::setCampaignId, form.getCampaignId())
                .set(Campaign::setTitle, form.getTitle())
                .set(Campaign::setStatement, form.getStatement())
                .set(Campaign::setGoal, form.getGoal())
                .build();
        cDao.insert(cam);

/*
        // メールアドレス重複チェック
        if (userDao.countByEmail(form.getEmail()) != 0) {
            form.setErrors(Multimap.of("email", EMAIL_ALREADY_EXISTS));
            return templateEngine.render("register",
                    "user", form
            );
        }

        User user = builder(new User())
                .set(User::setLastName, form.getLastName())
                .set(User::setFirstName, form.getFirstName())
                .set(User::setEmail, form.getEmail())
                .set(User::setPass, form.getPass())
                .build();
        userDao.insert(user);

        Session session = new Session();
        User loginUser = userDao.selectByEmail(form.getEmail());
        session.put(
                "principal",
                new LoginUserPrincipal(loginUser.getUserId(), loginUser.getLastName() + " " + loginUser.getFirstName())
        );
*/
        return builder(redirect("/", SEE_OTHER)).build();
    }
}

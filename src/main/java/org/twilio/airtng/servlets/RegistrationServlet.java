package org.twilio.airtng.servlets;


import org.twilio.airtng.lib.auth.SessionManager;
import org.twilio.airtng.lib.web.request.validators.RequestParametersValidator;
import org.twilio.airtng.models.User;
import org.twilio.airtng.repositories.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegistrationServlet extends BaseServlet {

    private final SessionManager sessionManager;
    private final UserRepository userRepository;

    @SuppressWarnings("unused")
    public RegistrationServlet() {
        this(new SessionManager(), new UserRepository());
    }

    public RegistrationServlet(SessionManager sessionManager, UserRepository userService) {
        this.sessionManager = sessionManager;
        this.userRepository = userService;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/registration.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        super.doPost(request, response);

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String countryCode = request.getParameter("countryCode");
        String phoneNumber = request.getParameter("phoneNumber");

        if (isValidRequest()) {
            String encryptedPassword = getPassWordEncryptor().encryptPassword(password);

            User user = userRepository.create(new User(name, email, encryptedPassword, countryCode, phoneNumber));

            sessionManager.logIn(request, user.getId());
            response.sendRedirect("/login");
        } else {
            preserveStatusRequest(request, name, email, countryCode, phoneNumber);
            request.getRequestDispatcher("/registration.jsp").forward(request, response);
        }
    }

    @Override
    protected boolean isValidRequest(RequestParametersValidator validator) {

        return validator.validatePresence("name", "email", "password", "countryCode", "phoneNumber")
                && validator.validateEmail("email");
    }

    private void preserveStatusRequest(
            HttpServletRequest request,
            String name,
            String email,
            String countryCode,
            String phoneNumber) {
        request.setAttribute("name", name);
        request.setAttribute("email", email);
        request.setAttribute("countryCode", countryCode);
        request.setAttribute("phoneNumber", phoneNumber);
    }
}
package travel.ways.travelwaysapi._bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.model.Roles;
import travel.ways.travelwaysapi.trip.model.db.expense.Currency;
import travel.ways.travelwaysapi.trip.model.db.expense.CurrencyEnum;
import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategory;
import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategoryEnum;
import travel.ways.travelwaysapi.trip.repository.CurrencyRepository;
import travel.ways.travelwaysapi.trip.repository.ExpenseCategoryRepository;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.repository.RoleRepository;
import travel.ways.travelwaysapi.user.service.shared.AccountService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

@Configuration
@RequiredArgsConstructor
public class Bootstrap {
    private final AccountService accountService;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final CurrencyRepository currencyRepository;

    @Bean
    @Transactional
    public CommandLineRunner setupRoles() {
        return args -> {
            for (var roleName : Roles.GetAllRoles()) {
                if (!roleRepository.existsByName(roleName)) {
                    roleRepository.save(new Role(roleName));
                }
            }
        };
    }

    @Bean
    @Transactional
    public CommandLineRunner setupExpenseCategories() {
        return args -> {
            for (var expenseCategoryEnum : ExpenseCategoryEnum.values()) {
                if (!expenseCategoryRepository.existsByExpenseCategory(expenseCategoryEnum)) {
                    expenseCategoryRepository.save(new ExpenseCategory((long) expenseCategoryEnum.getValue(),
                            expenseCategoryEnum));
                }
            }
        };
    }

    @Bean
    @Transactional
    public CommandLineRunner setupCurrencies() {
        return args -> {
            for (var currency : CurrencyEnum.values()) {
                if (!currencyRepository.existsByCurrency(currency)) {
                    currencyRepository.save(new Currency((long) currency.getValue(), currency));
                }
            }
        };
    }

    @Bean
    @Profile("dev")
    @DependsOn({"setupRoles"})
    public CommandLineRunner run() {
        return args -> {
            var createUser = new CreateUserRequest(
                    "John",
                    "Doe",
                    "JD",
                    "elo",
                    "test@example.com"
            );
            if (userService.getByUsername(createUser.getUsername()) == null) {
                AppUser user = accountService.createUser(createUser);
                accountService.activateUser(user.getHash());
            }

            var createUser2 = new CreateUserRequest(
                    "John_2",
                    "Doe_2",
                    "JD_2",
                    "elo",
                    "test2@example.com"
            );
            if (userService.getByUsername(createUser2.getUsername()) == null) {
                AppUser user = accountService.createUser(createUser2);
                accountService.activateUser(user.getHash());
            }

        };
    }
}

package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.service.YearlyDividendService;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@GraphQLApi
public class YearlyDividendController {

    private final YearlyDividendService yearlyDividendService;
}

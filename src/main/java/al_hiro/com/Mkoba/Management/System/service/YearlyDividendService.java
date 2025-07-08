package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.repository.YearlyDividendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log
public class YearlyDividendService {

    private final YearlyDividendRepository yearlyDividendRepository;
}

package co.vasic.card;

import co.vasic.user.UserInterface;

import java.util.List;

public interface PackageServiceInterface {
    PackageInterface getPackage(int id);

    PackageInterface getRandomPackage();

    List<PackageInterface> getPackages();

    PackageInterface addPackage(PackageInterface cardPackage);

    boolean deletePackage(int id);

    boolean addPackageToUser(PackageInterface cardPackage, UserInterface user);
}

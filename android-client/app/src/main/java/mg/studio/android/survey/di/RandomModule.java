package mg.studio.android.survey.di;

import java.util.Random;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides random number generators for dependency injection.
 */
@Module
public class RandomModule {

    /**
     * Constructs a new instance of RandomModule.
     * @param random The random number generator to provide for injection.
     */
    public RandomModule(Random random) {
        this.random = random;
    }

    @Provides
    @Singleton
    Random provideRandom() {
        return random;
    }

    private Random random;
}

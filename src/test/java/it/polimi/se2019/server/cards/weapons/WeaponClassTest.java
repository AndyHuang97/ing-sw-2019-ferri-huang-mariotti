package it.polimi.se2019.server.cards.weapons;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.games.player.AmmoColor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class WeaponClassTest {

    Weapon w;

    @Before
    public void setUp()  {
        w = new Weapon(null,"Power Glove", null, null,null);
    }

    @After
    public void tearDown()  {
        w = null;
    }

    @Test
    public void testSetter() {
        ActionUnit actionUnit = new ActionUnit(true, "Damage Target", "", null, null, false, 0, 0, false);
        List<ActionUnit> expectedAUList = Arrays.asList(actionUnit);
        List<AmmoColor> expectedAmmoColorList = Arrays.asList(AmmoColor.BLUE);

        w.setActionUnitList(expectedAUList);
        Assert.assertEquals(expectedAUList, w.getActionUnitList());
        w.setOptionalEffectList(expectedAUList);
        Assert.assertEquals(expectedAUList, w.getOptionalEffectList());
        w.setPickUpCost(expectedAmmoColorList);
        Assert.assertEquals(expectedAmmoColorList, w.getPickUpCost());
        w.setReloadCost(expectedAmmoColorList);
        Assert.assertEquals(expectedAmmoColorList, w.getReloadCost());
        w.setLoaded(true);
        Assert.assertEquals(true, w.isLoaded());

    }

}
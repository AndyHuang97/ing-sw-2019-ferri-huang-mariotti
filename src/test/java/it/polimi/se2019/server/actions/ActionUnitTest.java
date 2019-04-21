package it.polimi.se2019.server.actions;

import it.polimi.se2019.server.actions.conditions.Condition;
import it.polimi.se2019.server.actions.effects.Effect;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ActionUnitTest {

    ActionUnit actionUnit;

    @Before
    public void setUp() {
        actionUnit = new ActionUnit("action", null, null);
    }

    @After
    public void tearDown() {
        actionUnit = null;
    }

    @Test
    public void check() {
    }

    @Test
    public void run() {
    }

    @Test
    public void testSetLimited() {

        actionUnit.setLimited(true);

        Assert.assertEquals(true, actionUnit.isLimited());
    }

    @Test
    public void testSetName() {

        actionUnit.setName("actionName");

        Assert.assertEquals("actionName", actionUnit.getName());
    }

    @Test
    public void testSetDescription() {

        actionUnit.setDescription("This is a description");

        Assert.assertEquals("This is a description", actionUnit.getDescription());
    }

    @Test
    public void testSetEffectList() {

        ArrayList<Effect> effectList = new ArrayList<>();

        actionUnit.setEffectList(effectList);

        Assert.assertEquals(effectList, actionUnit.getEffectList());
    }

    @Test
    public void testSetConditionList() {

        ArrayList<Condition> conditionList = new ArrayList<>();

        actionUnit.setConditionList(conditionList);

        Assert.assertEquals(conditionList, actionUnit.getConditionList());
    }
}
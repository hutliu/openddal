/*
 * Copyright 2014-2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the “License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.openddal.command.expression;

import com.openddal.command.Parser;
import com.openddal.dbobject.table.ColumnResolver;
import com.openddal.dbobject.table.TableFilter;
import com.openddal.engine.Session;
import com.openddal.message.DbException;
import com.openddal.value.Value;

/**
 * A user-defined variable, for example: @ID.
 */
public class Variable extends Expression {

    private final String name;
    private Value lastValue;

    public Variable(Session session, String name) {
        this.name = name;
        lastValue = session.getVariable(name);
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public int getDisplaySize() {
        return lastValue.getDisplaySize();
    }

    @Override
    public long getPrecision() {
        return lastValue.getPrecision();
    }

    @Override
    public String getSQL() {
        return "@" + Parser.quoteIdentifier(name);
    }

    @Override
    public int getScale() {
        return lastValue.getScale();
    }

    @Override
    public int getType() {
        return lastValue.getType();
    }

    @Override
    public Value getValue(Session session) {
        lastValue = session.getVariable(name);
        return lastValue;
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
            case ExpressionVisitor.EVALUATABLE:
                // the value will be evaluated at execute time
            case ExpressionVisitor.READONLY:
            case ExpressionVisitor.INDEPENDENT:
            case ExpressionVisitor.NOT_FROM_RESOLVER:
            case ExpressionVisitor.QUERY_COMPARABLE:
            case ExpressionVisitor.GET_DEPENDENCIES:
            case ExpressionVisitor.GET_COLUMNS:
            case ExpressionVisitor.GET_AGGREGATE:
                return true;
            case ExpressionVisitor.DETERMINISTIC:
                return false;
            default:
                throw DbException.throwInternalError("type=" + visitor.getType());
        }
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level) {
        // nothing to do
    }

    @Override
    public Expression optimize(Session session) {
        return this;
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean value) {
        // nothing to do
    }

    @Override
    public void updateAggregate(Session session) {
        // nothing to do
    }

    public String getName() {
        return name;
    }

}
